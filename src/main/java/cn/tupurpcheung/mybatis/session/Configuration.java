package cn.tupurpcheung.mybatis.session;

import cn.tupurpcheung.mybatis.binding.MapperRegistry;
import cn.tupurpcheung.mybatis.datasource.DataSourceFactory;
import cn.tupurpcheung.mybatis.datasource.druid.H2DataSourceFactory;
import cn.tupurpcheung.mybatis.mapping.Environment;
import cn.tupurpcheung.mybatis.mapping.MappedStatement;
import cn.tupurpcheung.mybatis.mapping.TypeAliasRegistry;
import cn.tupurpcheung.mybatis.transaction.TransactionFactory;
import cn.tupurpcheung.mybatis.transaction.jdbc.JdbcTransactionFactory;

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


    //环境
    protected Environment environment;

    protected TypeAliasRegistry<DataSourceFactory> dataSourceFactoryTypeAliasRegistry = new TypeAliasRegistry<>();
    protected TypeAliasRegistry<TransactionFactory> transactionFactoryTypeAliasRegistry = new TypeAliasRegistry<>();

    public Configuration() {
        //数据源
        dataSourceFactoryTypeAliasRegistry.registerAlias("h2", H2DataSourceFactory.class);
        //事务管理器
        transactionFactoryTypeAliasRegistry.registerAlias("jdbc", JdbcTransactionFactory.class);

    }


    public void addMappedStatement(MappedStatement ms) {
        mappedStatements.put(ms.getId(), ms);
    }

    public <T> void addMapper(Class<T> type) {
        mapperRegistry.buildMapperProxyFactory(type);
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapperProxy(type, sqlSession);
    }


    public MappedStatement getMappedStatement(String id) {
        return mappedStatements.get(id);
    }


    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }


    public MapperRegistry getMapperRegistry() {
        return mapperRegistry;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public TypeAliasRegistry getDataSourceTypeAliasRegistry() {
        return dataSourceFactoryTypeAliasRegistry;
    }

    public TypeAliasRegistry getTransactionTypeAliasRegistry() {
        return transactionFactoryTypeAliasRegistry;
    }
}
