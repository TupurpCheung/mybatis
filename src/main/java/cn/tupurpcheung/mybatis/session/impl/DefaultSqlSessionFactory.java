package cn.tupurpcheung.mybatis.session.impl;

import cn.tupurpcheung.mybatis.binding.MapperRegistry;
import cn.tupurpcheung.mybatis.session.SqlSession;
import cn.tupurpcheung.mybatis.session.SqlSessionFactory;

public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private MapperRegistry mapperRegistry;

    public DefaultSqlSessionFactory(MapperRegistry mapperRegistry) {
        this.mapperRegistry = mapperRegistry;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(mapperRegistry);
    }
}
