package cn.tupurpcheung.mybatis.datasource.pooled;

import java.util.ArrayList;
import java.util.List;

public class PoolState {
    protected PooledDataSource pooledDataSource;
    //用户获取连接时，若无空闲连接，则创建一个连接，并放入活跃连接中
    //空闲连接
    protected final List<PooledConnection> idleConnectionList = new ArrayList<>();
    //活跃连接
    protected final List<PooledConnection> activeConnectionList = new ArrayList<>();

    //从连接池中获取连接的次数
    protected long requestCount = 0;
    //从连接池中获取连接时，获取连接的总耗时
    protected long accumulatedRequestTime = 0;

    // 总等待时间
    protected long accumulatedWaitTime = 0;
    // 要等待的次数
    protected long hadToWaitCount = 0;

    // 失败连接次数
    protected long badConnectionCount = 0;

    //连接执行总耗时
    protected long accumulatedCheckoutTime = 0;

    //执行连接超时的次数
    protected long claimedOverdueConnectionCount = 0;
    //超时时间累加值
    protected long accumulatedCheckoutTimeOfOverdueConnections = 0;



    public PoolState(PooledDataSource pooledDataSource) {
        this.pooledDataSource = pooledDataSource;
    }

    //执行平均耗时时间
    public synchronized long getAverageRequestTime() {
        return requestCount == 0 ? 0 : accumulatedRequestTime / requestCount;
    }

    //平均等待时间
    public synchronized long getAverageWaitTime() {
        return hadToWaitCount == 0 ? 0 : accumulatedWaitTime / hadToWaitCount;
    }

    public long getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(long requestCount) {
        this.requestCount = requestCount;
    }

    public long getAccumulatedRequestTime() {
        return accumulatedRequestTime;
    }

    public void setAccumulatedRequestTime(long accumulatedRequestTime) {
        this.accumulatedRequestTime = accumulatedRequestTime;
    }

    public long getAccumulatedCheckoutTime() {
        return accumulatedCheckoutTime;
    }

    public void setAccumulatedCheckoutTime(long accumulatedCheckoutTime) {
        this.accumulatedCheckoutTime = accumulatedCheckoutTime;
    }

    public long getClaimedOverdueConnectionCount() {
        return claimedOverdueConnectionCount;
    }

    public void setClaimedOverdueConnectionCount(long claimedOverdueConnectionCount) {
        this.claimedOverdueConnectionCount = claimedOverdueConnectionCount;
    }

    public long getAccumulatedCheckoutTimeOfOverdueConnections() {
        return accumulatedCheckoutTimeOfOverdueConnections;
    }

    public void setAccumulatedCheckoutTimeOfOverdueConnections(long accumulatedCheckoutTimeOfOverdueConnections) {
        this.accumulatedCheckoutTimeOfOverdueConnections = accumulatedCheckoutTimeOfOverdueConnections;
    }

    public long getAccumulatedWaitTime() {
        return accumulatedWaitTime;
    }

    public void setAccumulatedWaitTime(long accumulatedWaitTime) {
        this.accumulatedWaitTime = accumulatedWaitTime;
    }

    public long getHadToWaitCount() {
        return hadToWaitCount;
    }

    public void setHadToWaitCount(long hadToWaitCount) {
        this.hadToWaitCount = hadToWaitCount;
    }

    public long getBadConnectionCount() {
        return badConnectionCount;
    }

    public void setBadConnectionCount(long badConnectionCount) {
        this.badConnectionCount = badConnectionCount;
    }
}
