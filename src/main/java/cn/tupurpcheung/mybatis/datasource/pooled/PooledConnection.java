package cn.tupurpcheung.mybatis.datasource.pooled;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 可以简单理解是连接
 * 池化代理的链接
 * 由于会对连接进行关闭，需要处理池子里连接情况处理，所以延伸出PooledConnection代理去解决这个问题
 * 所以就需要进行代理，调用方法就会进入invoke()，如果关闭就可以进入pushConnection方法处理池中操作
 */

public class PooledConnection implements InvocationHandler {
    //被代理的接口
    private static final Class<?>[] INTERFACE_CLASS = new Class<?>[]{Connection.class};
    //需要被拦截代理的方法
    private static final String REAL_PROXY_METHOD = "close";

    private int hashCode = 0;

    private PooledDataSource dataSource;

    //真实的连接
    private Connection realConnection;
    // 代理连接
    private Connection proxyConnection;
    //从连接池中取出连接时的时间戳
    private Long checkoutTimestamp;
    // 数据库连接创建时间戳
    private Long createdTimestamp;
    // 数据库连接最后使用时间戳
    private Long lastUsedTimestamp;
    // 当前连接 url，用户名，密码的hashcode码= (url+username+password).hashcode()
    private int connectionTypeCode;
    // 是否有效
    private boolean valid;


    public PooledConnection(Connection realConnection,PooledDataSource dataSource) {
        this.hashCode = realConnection.hashCode();

        this.realConnection = realConnection;
        this.dataSource = dataSource;

        this.createdTimestamp = System.currentTimeMillis();
        this.lastUsedTimestamp = System.currentTimeMillis();

        this.valid = true;
        this.proxyConnection = (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(),INTERFACE_CLASS,this);
    }




    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();

        if (REAL_PROXY_METHOD.equals(methodName) && REAL_PROXY_METHOD.hashCode() == methodName.hashCode()) {
            //close方法代理拦截，回收连接
            dataSource.pushConnection(this);
            return null;
        } else {
            //执行connection的方法之前，检查下连接
            if (!Object.class.equals(method.getDeclaringClass())) {
                checkConnection();
            }

            return method.invoke(realConnection, args);
        }


    }

    private void checkConnection() throws SQLException {
        if (!valid) {
            throw new SQLException("Error accessing PooledConnection. Connection is invalid.");
        }

    }
    // 是否连接失效的判断
    public boolean isValid() {
        return valid && realConnection != null && dataSource.pingConnection(this);
    }

    // 连接失效设置false
    public void inValidate() {
        valid = false;
    }


    public int getRealHashCode() {
        return realConnection == null ? 0 : realConnection.hashCode();
    }

    //连接已空闲时间
    public long getTimeElapsedSinceLastUse() {
        return System.currentTimeMillis() - lastUsedTimestamp;
    }

    //连接已被从连接池中取出多久
    public long getCheckoutTime() {
        return System.currentTimeMillis() - checkoutTimestamp;
    }

    public Connection getRealConnection() {
        return realConnection;
    }

    public Connection getProxyConnection() {
        return proxyConnection;
    }

    public int getConnectionTypeCode() {
        return connectionTypeCode;
    }

    public void setConnectionTypeCode(int connectionTypeCode) {
        this.connectionTypeCode = connectionTypeCode;
    }

    public Long getCheckoutTimestamp() {
        return checkoutTimestamp;
    }

    public void setCheckoutTimestamp(Long checkoutTimestamp) {
        this.checkoutTimestamp = checkoutTimestamp;
    }

    public Long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Long getLastUsedTimestamp() {
        return lastUsedTimestamp;
    }

    public void setLastUsedTimestamp(Long lastUsedTimestamp) {
        this.lastUsedTimestamp = lastUsedTimestamp;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PooledConnection) {
            return realConnection.hashCode() == (((PooledConnection) obj).realConnection.hashCode());
        } else if (obj instanceof Connection) {
            return hashCode == obj.hashCode();
        } else {
            return false;
        }
    }


}
