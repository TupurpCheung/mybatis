package cn.tupurpcheung.mybatis.version1;

import cn.tupurpcheung.mybatis.io.Resources;
import cn.tupurpcheung.mybatis.session.SqlSession;
import cn.tupurpcheung.mybatis.session.SqlSessionFactory;
import cn.tupurpcheung.mybatis.session.SqlSessionFactoryBuilder;
import cn.tupurpcheung.mybatis.version1.mapper.UserMapper;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;

public class ApiTest {


    @Test
    public void test_parse_xml() throws IOException {
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

        String res = userMapper.queryUserName("tupurpcheung");
        System.out.println(res);
    }
}
