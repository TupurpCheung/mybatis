package cn.tupurpcheung.mybatis.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 事务定义
 */
public interface Transaction {
    Connection getConnection() throws SQLException;

    void commit() throws SQLException;

    void rollback() throws SQLException;

    void close() throws SQLException;
}
