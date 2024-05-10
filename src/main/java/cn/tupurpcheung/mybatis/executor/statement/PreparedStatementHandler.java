package cn.tupurpcheung.mybatis.executor.statement;

import cn.tupurpcheung.mybatis.executor.Executor;
import cn.tupurpcheung.mybatis.executor.resultsets.ResultSetHandler;
import cn.tupurpcheung.mybatis.mapping.MappedStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class PreparedStatementHandler extends BaseStatementHandler {
    public PreparedStatementHandler(Executor executor, MappedStatement mappedStatement, Object[] parameterObject) {
        super(executor, mappedStatement, parameterObject);
    }

    @Override
    protected Statement instantiateStatement(Connection connection) throws SQLException {

        return connection.prepareStatement(boundSql.getSql());

    }

    @Override
    public void parameterize(Statement statement) throws SQLException {
        PreparedStatement ps = (PreparedStatement) statement;
        //todo 多参数处理
        ps.setLong(1, Long.parseLong(parameterObject[0].toString()));

    }

    @Override
    public <E> List<E> query(Statement statement, ResultSetHandler resultSetHandler) throws SQLException {
        PreparedStatement ps = (PreparedStatement) statement;
        ps.execute();
        return resultSetHandler.<E>handleResultSets(ps);

    }
}
