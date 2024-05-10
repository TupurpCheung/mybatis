package cn.tupurpcheung.mybatis;

import cn.tupurpcheung.mybatis.datasource.pooled.PooledDataSource;
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
    public void setup() throws Exception {


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
        Assert.assertEquals("tupurpcheung", userInfo.getName());
        userInfo = userInfoMapper.queryUserById(2);
        System.out.println(userInfo);
        Assert.assertEquals("Bob", userInfo.getName());


    }

    //PooledDataSource 获取连接后放入活跃池中，若close，则会放入空闲池，如果空闲池满，则关闭真实连接
    //本方法测试拿到连接后，close连接（实际被PooledConnection代理了，执行PooledDataSource.pushConnection方法，放入空闲池）
    //再次获取连接，实际返回的还是之前的连接
    @Test
    public void test_pooled_with_close_pooledConnection() throws Exception {
        PooledDataSource pooledDataSource = new PooledDataSource();
        pooledDataSource.setDriver("org.h2.Driver");
        pooledDataSource.setUrl("jdbc:h2:mem:mybatis;DB_CLOSE_DELAY=-1");
        pooledDataSource.setUsername("root");
        pooledDataSource.setPassword("");

        while (true) {
            Connection connection = pooledDataSource.getConnection();
            System.out.println(connection);
            Thread.sleep(1000);
            //归还连接
            connection.close();
        }

        //测试循环获取连接
        //第一次获取连接放入活跃池中，close后，放入空闲池，如果空闲池满，则关闭真实连接


    }
    //PooledDataSource 获取连接后放入活跃池中，若close，则会放入空闲池，如果空闲池满，则关闭真实连接
    //本方法测试拿到连接后，不close连接（实际被PooledConnection代理了，执行PooledDataSource.pushConnection方法，放入空闲池）
    @Test
    public void test_pooled_with_not_close_pooledConnection() throws Exception {
        PooledDataSource pooledDataSource = new PooledDataSource();
        pooledDataSource.setDriver("org.h2.Driver");
        pooledDataSource.setUrl("jdbc:h2:mem:mybatis;DB_CLOSE_DELAY=-1");
        pooledDataSource.setUsername("root");
        pooledDataSource.setPassword("");

        while (true) {
            Connection connection = pooledDataSource.getConnection();
            System.out.println(connection);
            Thread.sleep(1000);
            //归还连接
            //connection.close();
        }

    }

}
