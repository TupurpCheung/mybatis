package cn.tupurpcheung.mybatis.datasource.pooled;

import cn.tupurpcheung.mybatis.datasource.unpooled.UnpooledDataSource;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.logging.Logger;

public class PooledDataSource implements DataSource {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(PooledDataSource.class);

    // 池状态
    private final PoolState state = new PoolState(this);

    private final UnpooledDataSource dataSource;

    // 最大活跃连接数
    protected int poolMaximumActiveConnections = 10;
    // 最大空闲连接数
    protected int poolMaximumIdleConnections = 5;

    //池化连接，单次使用时，最长可使用时间，超过此时间，池化连接可能会被强制回收
    protected int poolMaximumCheckoutTime = 20000;

    //从池中获取连接时，若没有连接，等待多久，再次尝试获取连接
    protected  int poolTimeToWait = 20000;

    // 开启或禁用侦测查询
    protected boolean poolPingEnabled = false;
    // 用来配置 poolPingQuery 多次执行一次
    protected int poolPingConnectionsNotUsedFor = 0;
    //数据库的侦测查询
    protected String poolPingQuery = "NO PING QUERY SET";

    // 已关闭的连接hash码
    private int expectedConnectionTypeCode;

    public PooledDataSource() {
        this.dataSource = new UnpooledDataSource();
    }

    /**
     * 回收链接
     * 1、从活跃连接中一处
     * 2、判断空闲池里的空闲连接是否大于最大连接，否则创建空闲连接对象，放入空闲池
     * 3、若空闲池已满，则关闭真实连接
     */
    protected void pushConnection(PooledConnection connection) throws SQLException {
        synchronized (state) {
            state.activeConnectionList.remove(connection);
            // 判断链接是否有效
            if (connection.isValid()) {
                // 如果空闲链接小于设定数量，也就是太少时，并且当前的连接哈希值==被关闭后的连接值
                if (state.idleConnectionList.size() < poolMaximumIdleConnections && connection.getConnectionTypeCode() == expectedConnectionTypeCode) {

                    if (!connection.getRealConnection().getAutoCommit()) {
                        connection.getRealConnection().rollback();
                    }
                    // 实例化一个新的DB连接，加入到idle列表
                    PooledConnection newConnection = new PooledConnection(connection.getRealConnection(), this);
                    state.idleConnectionList.add(newConnection);
                    newConnection.setCreatedTimestamp(connection.getCreatedTimestamp());
                    newConnection.setLastUsedTimestamp(connection.getLastUsedTimestamp());
                    connection.inValidate();
                    logger.info("Returned connection with real hashcode is :" + newConnection.getRealHashCode() + " to pool.");

                    // 通知其他线程（阻塞在popConnection方法的线程）可以来抢DB连接了
                    state.notifyAll();
                }
                // 否则，空闲链接还比较充足，将真实连接关闭
                else {

                    if (!connection.getRealConnection().getAutoCommit()) {
                        connection.getRealConnection().rollback();
                    }
                    // 将connection关闭
                    connection.getRealConnection().close();
                    logger.info("Closed connection with real hashcode is : " + connection.getRealHashCode() + ".");
                    connection.inValidate();
                }
            } else {
                logger.info("A bad connection with real hashcode is : " + connection.getRealHashCode() + " attempted to return to the pool, discarding connection.");

            }
        }
    }

    /**
     * 获取连接
     * 判断是否有空闲连接，如果有则返回空闲池里的第一个
     * 没有空闲连接则判断是否活跃池连接小于最大活跃连接数，没有创建新的连接
     * 如果活跃度已满从活跃池里取出最老的一个判断时间是否超过最大检测时间，是则移除
     * 随后创建新的连接，如果活跃池满了还没有超过检测时间则等待，最后拿到连接做个记录，没有拿到记录失败回馈
     */
    private PooledConnection popConnection(String username, String password) throws SQLException {
        boolean countedWait = false;
        PooledConnection conn = null;
        long t = System.currentTimeMillis();
        int localBadConnectionCount = 0;

        //一定要拿到一个连接
        while (conn == null) {
            synchronized (state) {
                // 如果有空闲链接：返回第一个
                if (!state.idleConnectionList.isEmpty()) {
                    conn = state.idleConnectionList.remove(0);
                    logger.info("Checked out connection with real hashcode is : " + conn.getRealHashCode() + " from pool.");
                }
                // 如果无空闲链接：创建新的链接
                else {
                    // 活跃连接数不足
                    if (state.activeConnectionList.size() < poolMaximumActiveConnections) {
                        conn = new PooledConnection(dataSource.getConnection(), this);
                        logger.info("Created new connection with real hashcode is ：" + conn.getRealHashCode() + " .");
                    }
                    // 活跃连接数已满
                    else {
                        // 取得活跃链接列表的第一个，也就是最老的一个连接
                        PooledConnection oldestActiveConnection = state.activeConnectionList.get(0);
                        long longestCheckoutTime = oldestActiveConnection.getCheckoutTime();

                        // 如果checkout时间过长，则这个链接标记为过期,删掉最老的链接，然后重新实例化一个新的链接
                        if (longestCheckoutTime > poolMaximumCheckoutTime) {
                            state.claimedOverdueConnectionCount++;
                            state.accumulatedCheckoutTimeOfOverdueConnections += longestCheckoutTime;
                            state.accumulatedCheckoutTime += longestCheckoutTime;

                            //删除旧连接
                            state.activeConnectionList.remove(oldestActiveConnection);
                            if (!oldestActiveConnection.getRealConnection().getAutoCommit()) {
                                oldestActiveConnection.getRealConnection().rollback();
                            }
                            oldestActiveConnection.inValidate();
                            logger.info("Remove and invalidate overdue connection with real hashcode is : " + oldestActiveConnection.getRealHashCode() + " from the pool.");

                            //创建一个新PooledConnection连接
                            conn = new PooledConnection(oldestActiveConnection.getRealConnection(), this);
                            logger.info("Claimed new connection with real hashcode is ：" + conn.getRealHashCode() + " .");
                        }
                        // 如果checkout超时时间不够长（没有超过运行时间），则等待
                        else {
                            try {
                                if (!countedWait) {
                                    state.hadToWaitCount++;
                                    countedWait = true;
                                }
                                logger.info("Waiting as long as " + poolTimeToWait + " milliseconds for connection.");
                                long wt = System.currentTimeMillis();
                                //线程等待，阻塞在此处
                                state.wait(poolTimeToWait);
                                state.accumulatedWaitTime += System.currentTimeMillis() - wt;
                            } catch (InterruptedException e) {
                                break;
                            }
                        }

                    }
                }
                // 获得到链接
                if (conn != null) {
                    if (conn.isValid()) {
                        if (!conn.getRealConnection().getAutoCommit()) {
                            conn.getRealConnection().rollback();
                        }
                        //设置hash
                        conn.setConnectionTypeCode(assembleConnectionTypeCode(dataSource.getUrl(), username, password));
                        // 记录从连接池拿取连接的checkout时间
                        conn.setCheckoutTimestamp(System.currentTimeMillis());
                        //记录最后使用时间
                        conn.setLastUsedTimestamp(System.currentTimeMillis());

                        //放入活跃连接池
                        state.activeConnectionList.add(conn);
                        //从连接池获取连接次数+1
                        state.requestCount++;
                        //请求连接总耗时
                        state.accumulatedRequestTime += System.currentTimeMillis() - t;
                    } else {
                        logger.info("A valid connection with real hashcode is : " + conn.getRealHashCode() + " was returned from the pool, getting another connection.");
                        // 如果没拿到，统计信息：失败链接 +1
                        state.badConnectionCount++;
                        localBadConnectionCount++;
                        conn = null;
                        // 失败次数较多，抛异常
                        if (localBadConnectionCount > (poolMaximumIdleConnections + 3)) {
                            logger.debug("PooledDataSource: Could not get a good connection to the database.");
                            throw new SQLException("PooledDataSource: Could not get a good connection to the database.");
                        }
                    }
                }
            }
        }



        if (conn == null) {
            logger.debug("PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
            throw new SQLException("PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
        }

        return conn;
    }

    // 如果空闲和活跃连接池里有连接则移除并关闭操作
    public void forceCloseAll() {
        synchronized (state) {
            expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
            // 关闭活跃链接
            for (int i = state.activeConnectionList.size(); i > 0; i--) {
                try {
                    PooledConnection conn = state.activeConnectionList.remove(i - 1);
                    conn.inValidate();

                    Connection realConn = conn.getRealConnection();
                    if (!realConn.getAutoCommit()) {
                        realConn.rollback();
                    }
                    realConn.close();
                } catch (Exception ignore) {

                }
            }
            // 关闭空闲链接
            for (int i = state.idleConnectionList.size(); i > 0; i--) {
                try {
                    PooledConnection conn = state.idleConnectionList.remove(i - 1);
                    conn.inValidate();

                    Connection realConn = conn.getRealConnection();
                    if (!realConn.getAutoCommit()) {
                        realConn.rollback();
                    }
                    realConn.close();
                } catch (Exception ignore) {

                }
            }
            logger.info("PooledDataSource forcefully closed/removed all connections.");
        }
    }

    // 测试加入池中的连接是否可用
    protected boolean pingConnection(PooledConnection conn) {
        boolean result = true;

        try {
            result = !conn.getRealConnection().isClosed();
        } catch (SQLException e) {
            logger.info("Connection with real hashcode is : " + conn.getRealHashCode() + " is BAD: " + e.getMessage());
            result = false;
        }

        if (result) {
            //开启了侦测
            if (poolPingEnabled) {
                //侦察间隔大于0，且距上次侦察已经超过侦察间隔
                if (poolPingConnectionsNotUsedFor >= 0 && conn.getTimeElapsedSinceLastUse() > poolPingConnectionsNotUsedFor) {
                    try {
                        logger.info("Testing connection  with real hashcode is : " + conn.getRealHashCode() + " ...");
                        Connection realConn = conn.getRealConnection();
                        Statement statement = realConn.createStatement();
                        ResultSet resultSet = statement.executeQuery(poolPingQuery);
                        resultSet.close();
                        if (!realConn.getAutoCommit()) {
                            realConn.rollback();
                        }
                        conn.setLastUsedTimestamp(System.currentTimeMillis());
                        logger.info("Connection with real hashcode is : " + conn.getRealHashCode() + " is GOOD!");
                    } catch (Exception e) {
                        logger.warn("Execution of ping query '" + poolPingQuery + "' failed: " + e.getMessage());
                        try {
                            conn.getRealConnection().close();
                        } catch (SQLException ignore) {
                        }
                        result = false;
                        logger.warn("Connection with real hashcode is : " + conn.getRealHashCode() + " is BAD: " + e.getMessage());
                    }
                }
            }
        }

        return result;
    }

    public static Connection unwrapConnection(Connection conn) {
        if (Proxy.isProxyClass(conn.getClass())) {
            InvocationHandler handler = Proxy.getInvocationHandler(conn);
            if (handler instanceof PooledConnection) {
                return ((PooledConnection) handler).getRealConnection();
            }
        }
        return conn;
    }

    private int assembleConnectionTypeCode(String url, String username, String password) {
        return ("" + url + username + password).hashCode();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return popConnection(dataSource.getUsername(), dataSource.getPassword()).getProxyConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return popConnection(username, password).getProxyConnection();
    }

    protected void finalize() throws Throwable {
        forceCloseAll();
        super.finalize();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException(getClass().getName() + " is not a wrapper.");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter logWriter) throws SQLException {
        DriverManager.setLogWriter(logWriter);
    }

    @Override
    public void setLoginTimeout(int loginTimeout) throws SQLException {
        DriverManager.setLoginTimeout(loginTimeout);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }

    public void setDriver(String driver) {
        dataSource.setDriver(driver);
        forceCloseAll();
    }

    public void setUrl(String url) {
        dataSource.setUrl(url);
        forceCloseAll();
    }

    public void setUsername(String username) {
        dataSource.setUsername(username);
        forceCloseAll();
    }

    public void setPassword(String password) {
        dataSource.setPassword(password);
        forceCloseAll();
    }


    public void setDefaultAutoCommit(boolean defaultAutoCommit) {
        dataSource.setAutoCommit(defaultAutoCommit);
        forceCloseAll();
    }

}