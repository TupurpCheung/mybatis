package cn.tupurpcheung.mybatis.session.impl;

import cn.hutool.core.util.StrUtil;
import cn.tupurpcheung.mybatis.mapping.BoundSql;
import cn.tupurpcheung.mybatis.mapping.Environment;
import cn.tupurpcheung.mybatis.mapping.MappedStatement;
import cn.tupurpcheung.mybatis.session.Configuration;
import cn.tupurpcheung.mybatis.session.SqlSession;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DefaultSqlSession implements SqlSession {
    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }


    @Override
    public <T> T selectOne(String statement) {
        return (T) ("你被代理了！" + statement);
    }

    /**
     * @param statement 接口名.方法名
     */
    @Override
    public <T> T selectOne(String statement, Object[] parameters) {
        try {
            MappedStatement mappedStatement = configuration.getMappedStatement(statement);
            Environment environment = configuration.getEnvironment();
            Connection connection = null;

            connection = environment.getDataSource().getConnection();
            BoundSql boundSql = mappedStatement.getBoundSql();

            PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSql());
            //参数处理，根据位置无脑设置，未使用xml中#{xx}定义的key
            if (null != parameters) {
                int index = 1;
                for (Object obj : parameters) {
                    //todo 基本类型处理
                    if (obj instanceof String) {
                        preparedStatement.setString(index++, obj.toString());
                    } else if (obj instanceof Long) {
                        preparedStatement.setLong(index++, (Long) obj);
                    } else if (obj instanceof Integer) {
                        preparedStatement.setLong(index++, (Integer) obj);
                    }

                }
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            List<T> list = resultSet2Obj(resultSet, Class.forName(boundSql.getResultType()));
            return list.get(0);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    private <T> List<T> resultSet2Obj(ResultSet resultSet, Class<?> clazz) {
        List<T> list = new ArrayList();
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                // 新建实例
                T obj = (T) clazz.newInstance();
                for (int i = 1; i <= columnCount; i++) {
                    Object value = resultSet.getObject(i);
                    String columnName = metaData.getColumnName(i);
                    //全转小写后，转驼峰，首字母大写
                    String setMethod = "set" + StrUtil.upperFirst(StrUtil.toCamelCase(columnName.toLowerCase())) ;

                    // clazz.getMethod(setMethod, value.getClass());
                    // 参数传入实体的方法名称，和方法对应的参数class
                    Method method;
                    if (value instanceof Timestamp) {
                        method = clazz.getMethod(setMethod, Date.class);
                    } else {
                        method = clazz.getMethod(setMethod, value.getClass());
                    }
                    // 反射调用方法
                    method.invoke(obj, value);
                }
                list.add(obj);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


}
