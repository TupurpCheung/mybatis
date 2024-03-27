package cn.tupurpcheung.mybatis.session.impl;

import cn.tupurpcheung.mybatis.binding.MapperRegistry;
import cn.tupurpcheung.mybatis.session.SqlSession;

public class DefaultSqlSession implements SqlSession {

    private MapperRegistry mapperRegistry;

    public DefaultSqlSession(MapperRegistry mapperRegistry) {
        this.mapperRegistry = mapperRegistry;
    }

    @Override
    public <T> T selectOne(String statement) {
        return (T) ("你被代理了！" + statement);
    }

    @Override
    public <T> T selectOne(String statement, Object[] parameter) {
        return  (T) ("你被代理了！" + "方法：" + statement + " 入参：" + parameter[0]);
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return mapperRegistry.getMapper(type,this);
    }
}
