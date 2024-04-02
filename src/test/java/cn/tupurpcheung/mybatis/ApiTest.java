package cn.tupurpcheung.mybatis;

import cn.tupurpcheung.mybatis.io.Resources;
import cn.tupurpcheung.mybatis.mapper.UserInfoMapper;
import cn.tupurpcheung.mybatis.po.UserInfo;
import cn.tupurpcheung.mybatis.session.SqlSession;
import cn.tupurpcheung.mybatis.session.SqlSessionFactory;
import cn.tupurpcheung.mybatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ApiTest {


    @Before
    public void setup() throws Exception{


        Class.forName("org.h2.Driver");

        //url加上 DB_CLOSE_DELAY=-1 配置，保证在同一个jvm中数据不会消失
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:mybatis;DB_CLOSE_DELAY=-1", "root", "");

        // 创建一个表
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("CREATE TABLE user_info(id INT PRIMARY KEY, name VARCHAR(32), sex VARCHAR(2), age TINYINT)");

        // 插入数据
        stmt.executeUpdate("INSERT INTO user_info VALUES (1, 'Alice','女',15)");
        stmt.executeUpdate("INSERT INTO user_info VALUES (2, 'Bob','男',15)");
        stmt.executeUpdate("INSERT INTO user_info VALUES (3, 'tupurpcheung','男',25)");
        stmt.executeUpdate("INSERT INTO user_info VALUES (4, 'tupurpcheung','女',22)");



        // 查询数据
        ResultSet rs = stmt.executeQuery("SELECT * FROM user_info");
        while (rs.next()) {
            System.out.println("ID = " + rs.getInt("ID") + ", NAME = " + rs.getString("NAME"));
        }

        // 关闭连接
        conn.close();


    }

    @Test
    public void test_parse_xml() throws IOException {
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserInfoMapper userInfoMapper = sqlSession.getMapper(UserInfoMapper.class);

        UserInfo userInfo = userInfoMapper.queryUserById(3);
        System.out.println(userInfo);
        Assert.assertEquals("tupurpcheung",userInfo.getName());
        userInfo = userInfoMapper.queryUserById(2);
        System.out.println(userInfo);
        Assert.assertEquals("Bob",userInfo.getName());

        userInfo = userInfoMapper.queryUser("tupurpcheung",22);
        System.out.println(userInfo);
    }
}
