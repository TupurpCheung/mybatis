package cn.tupurpcheung.mybatis.session;

import cn.tupurpcheung.mybatis.builder.impl.XMLConfigBuilder;
import cn.tupurpcheung.mybatis.session.impl.DefaultSqlSessionFactory;

import java.io.Reader;


/**
 * 程序入口，解析资源到Configuration中，并创建 SqlSessionFactory
 */
public class SqlSessionFactoryBuilder {
    public SqlSessionFactory build(Reader reader) {
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(reader);
        return build(xmlConfigBuilder.parse());
    }


    public SqlSessionFactory build(Configuration config) {
        return new DefaultSqlSessionFactory(config);
    }

}
