package cn.tupurpcheung.mybatis.executor;

import cn.tupurpcheung.mybatis.executor.resultsets.ResultSetHandler;
import cn.tupurpcheung.mybatis.mapping.MappedStatement;
import cn.tupurpcheung.mybatis.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

public interface Executor {

    ResultSetHandler NO_RESULT_HANDLER = null;

    // 定义执行Sql查询操作
    /**
     * @param ms
     */
    <E> List<E> query(MappedStatement ms, Object[] parameter, ResultSetHandler resultSetHandler );

    Transaction getTransaction();

    // 以下事务处理-提交、回滚、关闭
    void commit(boolean required) throws SQLException;

    void rollback(boolean required) throws SQLException;

    void close(boolean forceRollback);


}
