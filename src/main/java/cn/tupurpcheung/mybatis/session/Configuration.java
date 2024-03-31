package cn.tupurpcheung.mybatis.session;

import cn.tupurpcheung.mybatis.binding.MapperRegistry;
import cn.tupurpcheung.mybatis.mapping.MappedStatement;

import java.util.HashMap;
import java.util.Map;

/**
 * 存放MapperRegistry 和 MappedStatement
 */
public class Configuration {

    //接口级别
    private MapperRegistry mapperRegistry = new MapperRegistry();

    //xml映射的语句，接口中的方法级别
    private Map<String, MappedStatement> mappedStatements = new HashMap<>();

    public void addMappedStatement(MappedStatement ms) {
        mappedStatements.put(ms.getId(), ms);
    }

    public <T> void addMapper(Class<T> type) {
        mapperRegistry.buildMapperProxyFactory(type);
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapperProxy(type, sqlSession);
    }

    public MapperRegistry getMapperRegistry() {
        return mapperRegistry;
    }

    public MappedStatement getMappedStatement(String id) {
        return mappedStatements.get(id);
    }


}
