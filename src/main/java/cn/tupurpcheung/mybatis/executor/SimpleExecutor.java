package cn.tupurpcheung.mybatis.executor;

import cn.tupurpcheung.mybatis.executor.resultsets.ResultSetHandler;
import cn.tupurpcheung.mybatis.executor.statement.StatementHandler;
import cn.tupurpcheung.mybatis.mapping.BoundSql;
import cn.tupurpcheung.mybatis.mapping.MappedStatement;
import cn.tupurpcheung.mybatis.session.Configuration;
import cn.tupurpcheung.mybatis.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SimpleExecutor extends BaseExecutor{

    public SimpleExecutor(Configuration configuration, Transaction transaction) {
        super(configuration, transaction);
    }

    @Override
    protected <E> List<E> doQuery(MappedStatement ms,Object[] parameter, ResultSetHandler resultSetHandler) {
        try {
            Configuration configuration = ms.getConfiguration();
            // 调用创建语句处理器-PreparedStatementHandler
            StatementHandler handler = configuration.newStatementHandler(this, ms, parameter);
            Connection connection = transaction.getConnection();
            // 调用语句处理器-准备操作，如初始化参数
            Statement stmt = handler.prepare(connection);
            // 设置参数
            handler.parameterize(stmt);
            // 调用语句处理器的查询方法
            return handler.query(stmt, resultSetHandler);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }


    }
}
