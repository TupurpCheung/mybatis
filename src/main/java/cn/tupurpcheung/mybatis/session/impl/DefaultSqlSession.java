package cn.tupurpcheung.mybatis.session.impl;

import cn.hutool.core.util.StrUtil;
import cn.tupurpcheung.mybatis.executor.Executor;
import cn.tupurpcheung.mybatis.executor.resultsets.DefaultResultSetHandler;
import cn.tupurpcheung.mybatis.executor.resultsets.ResultSetHandler;
import cn.tupurpcheung.mybatis.mapping.BoundSql;
import cn.tupurpcheung.mybatis.mapping.Environment;
import cn.tupurpcheung.mybatis.mapping.MappedStatement;
import cn.tupurpcheung.mybatis.session.Configuration;
import cn.tupurpcheung.mybatis.session.SqlSession;
import cn.tupurpcheung.mybatis.transaction.jdbc.JdbcTransaction;
import cn.tupurpcheung.mybatis.transaction.jdbc.JdbcTransactionFactory;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DefaultSqlSession implements SqlSession {
    private Configuration configuration;
    private Executor executor;



    public DefaultSqlSession(Configuration configuration, Executor executor) {

        this.configuration = configuration;
        this.executor = executor;
    }


    @Override
    public <T> T selectOne(String statement) {
        return (T) ("你被代理了！" + statement);
    }

    /**
     * @param statement 接口名.方法名
     */
    @Override
    public <T> T selectOne(String statement, Object[] parameters) {
        try {
            MappedStatement mappedStatement = configuration.getMappedStatement(statement);
            ResultSetHandler resultSetHandler = new DefaultResultSetHandler(mappedStatement);
            List<T> query = executor.query(mappedStatement, parameters, resultSetHandler);
            if(null == query || query.isEmpty()){
                return null;
            }
            return query.get(0);


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }




}
