package cn.tupurpcheung.mybatis.builder;

import cn.tupurpcheung.mybatis.session.Configuration;

public class BaseBuilder {

    protected final Configuration configuration;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    // 获取configuration
    public Configuration getConfiguration() {
        return configuration;
    }


}
