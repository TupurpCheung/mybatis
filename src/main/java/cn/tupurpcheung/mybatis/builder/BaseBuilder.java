package cn.tupurpcheung.mybatis.builder;

import cn.tupurpcheung.mybatis.datasource.DataSourceFactory;
import cn.tupurpcheung.mybatis.mapping.TypeAliasRegistry;
import cn.tupurpcheung.mybatis.session.Configuration;
import cn.tupurpcheung.mybatis.transaction.TransactionFactory;

public class BaseBuilder {

    protected TypeAliasRegistry typeAliasRegistry;

    protected Configuration configuration;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
        this.typeAliasRegistry = configuration.getTypeAliasRegistry();

    }


    // 获取configuration
    public Configuration getConfiguration() {
        return configuration;
    }


}
