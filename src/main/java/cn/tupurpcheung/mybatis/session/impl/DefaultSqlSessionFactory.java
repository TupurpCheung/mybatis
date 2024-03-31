package cn.tupurpcheung.mybatis.session.impl;

import cn.tupurpcheung.mybatis.session.Configuration;
import cn.tupurpcheung.mybatis.session.SqlSession;
import cn.tupurpcheung.mybatis.session.SqlSessionFactory;

/**
 * 创建session
 * */
public class DefaultSqlSessionFactory implements SqlSessionFactory {


    private Configuration configuration;


    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration);
    }
}
