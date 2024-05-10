package cn.tupurpcheung.mybatis.executor.statement;

import cn.tupurpcheung.mybatis.executor.Executor;
import cn.tupurpcheung.mybatis.executor.resultsets.ResultSetHandler;
import cn.tupurpcheung.mybatis.mapping.BoundSql;
import cn.tupurpcheung.mybatis.mapping.MappedStatement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class SimpleStatementHandler extends BaseStatementHandler {
    public SimpleStatementHandler(Executor executor, MappedStatement mappedStatement, Object[] parameterObject) {
        super(executor, mappedStatement, parameterObject);
    }

    @Override
    protected Statement instantiateStatement(Connection connection) throws SQLException {

        return connection.createStatement();

    }

    @Override
    public void parameterize(Statement statement) throws SQLException {


    }

    @Override
    public <E> List<E> query(Statement statement, ResultSetHandler resultSetHandler) throws SQLException {

        statement.execute(boundSql.getSql());
        return resultSetHandler.<E>handleResultSets(statement);

    }
}
