package cn.tupurpcheung.mybatis.builder;

import cn.tupurpcheung.mybatis.datasource.DataSourceFactory;
import cn.tupurpcheung.mybatis.mapping.TypeAliasRegistry;
import cn.tupurpcheung.mybatis.session.Configuration;
import cn.tupurpcheung.mybatis.transaction.TransactionFactory;

public class BaseBuilder {

    protected TypeAliasRegistry<DataSourceFactory> dataSourceFactoryTypeAliasRegistry;
    protected TypeAliasRegistry<TransactionFactory> transactionFactoryTypeAliasRegistry;
    protected Configuration configuration;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
        this.dataSourceFactoryTypeAliasRegistry = configuration.getDataSourceTypeAliasRegistry();
        this.transactionFactoryTypeAliasRegistry = configuration.getTransactionTypeAliasRegistry();

    }


    // 获取configuration
    public Configuration getConfiguration() {
        return configuration;
    }


}
