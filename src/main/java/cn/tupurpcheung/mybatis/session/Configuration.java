package cn.tupurpcheung.mybatis.session;

import cn.tupurpcheung.mybatis.binding.MapperRegistry;
import cn.tupurpcheung.mybatis.datasource.druid.H2DataSourceFactory;
import cn.tupurpcheung.mybatis.datasource.pooled.PooledDataSourceFactory;
import cn.tupurpcheung.mybatis.datasource.unpooled.UnpooledDataSourceFactory;
import cn.tupurpcheung.mybatis.executor.Executor;
import cn.tupurpcheung.mybatis.executor.SimpleExecutor;
import cn.tupurpcheung.mybatis.executor.resultsets.DefaultResultSetHandler;
import cn.tupurpcheung.mybatis.executor.resultsets.ResultSetHandler;
import cn.tupurpcheung.mybatis.executor.statement.PreparedStatementHandler;
import cn.tupurpcheung.mybatis.executor.statement.StatementHandler;
import cn.tupurpcheung.mybatis.mapping.BoundSql;
import cn.tupurpcheung.mybatis.mapping.Environment;
import cn.tupurpcheung.mybatis.mapping.MappedStatement;
import cn.tupurpcheung.mybatis.mapping.TypeAliasRegistry;
import cn.tupurpcheung.mybatis.transaction.Transaction;
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

    protected TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

    public Configuration() {
        //数据源
        typeAliasRegistry.registerAlias("h2", H2DataSourceFactory.class);
        typeAliasRegistry.registerAlias("unPooled", UnpooledDataSourceFactory.class);
        typeAliasRegistry.registerAlias("pooled", PooledDataSourceFactory.class);
        //todo 修改为mysql数据源工厂
        typeAliasRegistry.registerAlias("mysql", H2DataSourceFactory.class);
        //事务管理器
        typeAliasRegistry.registerAlias("jdbc", JdbcTransactionFactory.class);

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

    public TypeAliasRegistry getTypeAliasRegistry() {
        return typeAliasRegistry;
    }


    /**
     * 生产执行器
     */
    public Executor newExecutor(Transaction transaction) {
        return new SimpleExecutor(this, transaction);
    }


    public StatementHandler newStatementHandler(SimpleExecutor simpleExecutor, MappedStatement ms, Object[] parameter ) {
        return new PreparedStatementHandler(simpleExecutor,ms,parameter);
    }

    /**
     * 创建结果集处理器
     */
    public ResultSetHandler newResultSetHandler(MappedStatement mappedStatement) {
        return new DefaultResultSetHandler( mappedStatement);
    }



}
