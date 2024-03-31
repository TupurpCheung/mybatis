package cn.tupurpcheung.mybatis.builder.impl;

import cn.tupurpcheung.mybatis.builder.BaseBuilder;
import cn.tupurpcheung.mybatis.io.Resources;
import cn.tupurpcheung.mybatis.mapping.MappedStatement;
import cn.tupurpcheung.mybatis.mapping.SqlCommandType;
import cn.tupurpcheung.mybatis.session.Configuration;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
            mapperElement(root.element(MAPPERS_ELEMENT_NAME));
        } catch (Exception e) {
            //todo 异常处理
            e.printStackTrace();
        }

        return configuration;

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

            // 命名空间
            String namespace = root.attributeValue(NAMESPACE_ATTRIBUTE_NAME);

            // select
            List<Element> selectNodes = root.elements(SELECT_ELEMENT_NAME);
            for (Element node : selectNodes) {
                String id = node.attributeValue(ID_ATTRIBUTE_NAME);
                String parameterType = node.attributeValue(PARAMETER_TYPE_ATTRIBUTE_NAME);
                String resultType = node.attributeValue(RESULT_TYPE_ATTRIBUTE_NAME);
                String sql = node.getText();

                // ? 匹配参数
                Map<Integer, String> paramter = new HashMap<>();

                Matcher matcher = PARAMETER_PATTERN.matcher(sql);
                for (int i = 1; matcher.find(); i++) {
                    String g1 = matcher.group(1);
                    String g2 = matcher.group(2);
                    paramter.put(i, g2);
                    sql = sql.replace(g1, PARAMETER_PLACEHOLDER);
                }

                String msId = namespace + "." + id;
                String nodeName = node.getName();

                SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));

                MappedStatement mappedStatement = new MappedStatement.Builder(configuration, msId, sqlCommandType,
                        parameterType, resultType, sql, paramter).build();
                // 添加解析SQL
                configuration.addMappedStatement(mappedStatement);

            }
            // 注册代理注册器
            configuration.addMapper(Resources.classForName(namespace));
        }
    }
}
