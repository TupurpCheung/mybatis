package cn.tupurpcheung.mybatis.executor.statement;

import cn.tupurpcheung.mybatis.executor.Executor;
import cn.tupurpcheung.mybatis.executor.resultsets.ResultSetHandler;
import cn.tupurpcheung.mybatis.mapping.BoundSql;
import cn.tupurpcheung.mybatis.mapping.MappedStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class PreparedStatementHandler extends BaseStatementHandler{
    public PreparedStatementHandler(Executor executor, MappedStatement mappedStatement,  BoundSql boundSql,Object parameterObject) {
        super(executor, mappedStatement, boundSql,parameterObject );
    }

    @Override
    protected Statement instantiateStatement(Connection connection) throws SQLException {

        return connection.prepareStatement(boundSql.getSql());

    }

    @Override
    public void parameterize(Statement statement) throws SQLException {
        PreparedStatement ps = (PreparedStatement) statement;
        ps.setLong(1, Long.parseLong(((Object[]) parameterObject)[0].toString()));

    }

    @Override
    public <E> List<E> query(Statement statement, ResultSetHandler resultSetHandler) throws SQLException {
        PreparedStatement ps = (PreparedStatement) statement;
        ps.execute();
        return resultSetHandler.<E> handleResultSets(ps);

    }
}
