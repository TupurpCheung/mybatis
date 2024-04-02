package cn.tupurpcheung.mybatis;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class h2Test {


    /**
     * USER 为 h2 关键字
     * */
    @Test
    public void test_db_h2() throws Exception {

        Class.forName("org.h2.Driver");

        Connection conn = DriverManager.getConnection("jdbc:h2:mem:mybatis", "root", "");

        // 创建一个表
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("CREATE TABLE \"USER\"(ID INT PRIMARY KEY, NAME VARCHAR(32))");

        // 插入数据
        stmt.executeUpdate("INSERT INTO \"USER\" VALUES (1, 'Alice')");
        stmt.executeUpdate("INSERT INTO \"USER\" VALUES (2, 'Bob')");

        // 查询数据
        ResultSet rs = stmt.executeQuery("SELECT * FROM \"USER\"");
        while (rs.next()) {
            System.out.println("ID = " + rs.getInt("ID") + ", NAME = " + rs.getString("NAME"));
        }

        // 关闭连接
        conn.close();

    }
}
