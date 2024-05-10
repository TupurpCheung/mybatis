package cn.tupurpcheung.mybatis.builder.impl;

import cn.tupurpcheung.mybatis.builder.BaseBuilder;
import cn.tupurpcheung.mybatis.datasource.DataSourceFactory;
import cn.tupurpcheung.mybatis.io.Resources;
import cn.tupurpcheung.mybatis.mapping.BoundSql;
import cn.tupurpcheung.mybatis.mapping.Environment;
import cn.tupurpcheung.mybatis.mapping.MappedStatement;
import cn.tupurpcheung.mybatis.mapping.SqlCommandType;
import cn.tupurpcheung.mybatis.session.Configuration;
import cn.tupurpcheung.mybatis.transaction.TransactionFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import javax.sql.DataSource;
import java.io.Reader;
import java.util.*;
import java.util.regex.Matcher;

import static cn.tupurpcheung.mybatis.constants.XsdConstants.*;

public class XMLConfigBuilder extends BaseBuilder {

    private Element root;

    public XMLConfigBuilder(Reader reader) {

        super(new Configuration());

        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(reader);
            root = document.getRootElement();
        } catch (DocumentException e) {
            //todo 异常处理
            e.printStackTrace();

        }
    }

    /**
     * 解析配置
     */
    public Configuration parse() {

        try {
            // 环境解析
            environmentsElement(root.element(ENVIRONMENTS_ELEMENT_NAME));
            //mapper.xml解析
            mapperElement(root.element(MAPPERS_ELEMENT_NAME));
        } catch (Exception e) {
            //todo 异常处理
            e.printStackTrace();
        }

        return configuration;

    }

    // 解析数据源，并最终存储到环境类里
    private void environmentsElement(Element context) throws Exception {
        String defaultEnvironmentName = context.attributeValue(DEFAULT_ATTRIBUTE_NAME);
        List<Element> enviromentList = context.elements(ENVIRONMENT_ELEMENT_NAME);
        for (Element e : enviromentList) {
            String id = e.attributeValue(ID_ATTRIBUTE_NAME);
            if (defaultEnvironmentName.equals(id)) {
                // 数据源
                Element dataSourceElement = e.element(DATASOURCE_ELEMENT_NAME);
                // 从类型注册机里找到DRUID名字的并得到类DataSourceFactory
                DataSourceFactory dataSourceFactory =  (DataSourceFactory)typeAliasRegistry
                        .resolveAlias(dataSourceElement.attributeValue(TYPE_ATTRIBUTE_NAME)).newInstance();
                // 获取xml中数据源的属性数据
                List<Element> propertiesList = dataSourceElement.elements(PROPERTY_ELEMENT_NAME);
                Properties properties = new Properties();

                for (Element prop : propertiesList) {
                    properties.setProperty(prop.attributeValue(PROPERTIES_NAME_KEY), prop.attributeValue(PROPERTIES_VALUE_KEY));
                }
                // 设置数据源属性对象
                dataSourceFactory.setProperties(properties);
                // 设置DruidDataSource数据源并返回
                DataSource dataSource = dataSourceFactory.getDataSource();

                // todo 实现事务管理器
                TransactionFactory txFactory = (TransactionFactory) typeAliasRegistry.resolveAlias(e.element(TRANSACTION_MANAGER_ELEMENT_NAME).attributeValue(TYPE_ATTRIBUTE_NAME)).newInstance();

                // 构建环境-通过建造者模式
                Environment.Builder environmentBuilder = new Environment.Builder(id).dataSource(dataSource).transactionFactory(txFactory);

                // 将环境信息存储到配置里configuration供其他需要的地方使用
                configuration.setEnvironment(environmentBuilder.build());
            }
        }
    }


    // 将xml中的配置解析出来存储到对应的实体类中
    private void mapperElement(Element mappers) throws Exception {
        List<Element> mapperList = mappers.elements(MAPPER_ELEMENT_NAME);
        for (Element e : mapperList) {
            String resource = e.attributeValue(RESOURCE_ATTRIBUTE_NAME);
            Reader reader = Resources.getResourceAsReader(resource);
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new InputSource(reader));
            Element root = document.getRootElement();

            // 命名空间，接口维度
            String namespace = root.attributeValue(NAMESPACE_ATTRIBUTE_NAME);

            // select
            List<Element> selectNodes = root.elements(SELECT_ELEMENT_NAME);
            for (Element node : selectNodes) {
                String id = node.attributeValue(ID_ATTRIBUTE_NAME);
                String parameterType = node.attributeValue(PARAMETER_TYPE_ATTRIBUTE_NAME);
                String resultType = node.attributeValue(RESULT_TYPE_ATTRIBUTE_NAME);
                String sql = node.getText();

                // ? 匹配参数
                Map<Integer, String> paramters = new HashMap<>();

                Matcher matcher = PARAMETER_PATTERN.matcher(sql);
                for (int i = 1; matcher.find(); i++) {
                    String g1 = matcher.group(1);
                    String g2 = matcher.group(2);
                    paramters.put(i, g2);
                    sql = sql.replace(g1, PARAMETER_PLACEHOLDER);
                }

                //接口名.方法名
                String msId = namespace + "." + id;
                String nodeName = node.getName();

                SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));
                BoundSql boundSql = new BoundSql(sql, paramters, parameterType, resultType);
                //接口+方法 维度
                MappedStatement mappedStatement = new MappedStatement.Builder(configuration, msId, sqlCommandType,
                        boundSql).build();
                // 添加解析SQL
                configuration.addMappedStatement(mappedStatement);

            }
            // 注册代理注册器
            configuration.addMapper(Resources.classForName(namespace));
        }
    }
}
