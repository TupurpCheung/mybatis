package cn.tupurpcheung.mybatis.executor.statement;

import cn.tupurpcheung.mybatis.executor.Executor;
import cn.tupurpcheung.mybatis.executor.resultsets.ResultSetHandler;
import cn.tupurpcheung.mybatis.mapping.BoundSql;
import cn.tupurpcheung.mybatis.mapping.MappedStatement;
import cn.tupurpcheung.mybatis.session.Configuration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class BaseStatementHandler implements StatementHandler{

    protected final Configuration configuration;
    protected final Executor executor;
    protected final MappedStatement mappedStatement;

    protected final Object[] parameterObject;
    protected BoundSql boundSql;

    public BaseStatementHandler(Executor executor, MappedStatement mappedStatement, Object[] parameterObject) {
        this.executor = executor;
        this.mappedStatement = mappedStatement;
        this.parameterObject = parameterObject;
        this.configuration = mappedStatement.getConfiguration();
        this.boundSql = mappedStatement.getBoundSql();


    }

    @Override
    public Statement prepare(Connection connection) throws SQLException {
        Statement statement = null;
        try {
            // 实例化 Statement
            statement = instantiateStatement(connection);
            // 参数设置，可以被抽取，提供配置
            statement.setQueryTimeout(350);
            statement.setFetchSize(10000);
            return statement;
        } catch (Exception e) {
            throw new RuntimeException("Error preparing statement.  Cause: " + e, e);
        }
    }

    protected abstract Statement instantiateStatement(Connection connection) throws SQLException;


}
