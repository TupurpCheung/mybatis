package cn.tupurpcheung.mybatis.version1;

import cn.tupurpcheung.mybatis.binding.MapperRegistry;
import cn.tupurpcheung.mybatis.version1.mapper.UserMapper;
import cn.tupurpcheung.mybatis.version1.service.RoleService;
import cn.tupurpcheung.mybatis.session.SqlSession;
import cn.tupurpcheung.mybatis.session.impl.DefaultSqlSessionFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ApiTest {


    @Before
    public void setup() {
        //调整日志级别
        Configurator.setRootLevel(Level.DEBUG);
    }

    @Test
    public void test_MapperProxyFactory_version_1() {

        //扫描指定包下的接口，给每个接口创建MapperProxyFactory，存放到 MapperRegistry 的 cacheMappers 属性中。
        String packageName = "cn.tupurpcheung.mybatis.version1.mapper";
        MapperRegistry mapperRegistry = new MapperRegistry();
        mapperRegistry.addMappers(packageName);

        //sessionFactory工厂
        DefaultSqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(mapperRegistry);

        //获取session，将 mapperRegistry 属性也通过构造函数送给了SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //SqlSession 从 mapperRegistry 获取 代理类
        UserMapper mapperProxy = sqlSession.getMapper(UserMapper.class);
        String userName = mapperProxy.queryUserName("tony");
        Assert.assertTrue(userName.contains("tony"));
        
        RoleService roleService = null;
        try {
            roleService = sqlSession.getMapper(RoleService.class);
        } catch (RuntimeException e) {

        }
        Assert.assertNull(roleService);


    }
}
