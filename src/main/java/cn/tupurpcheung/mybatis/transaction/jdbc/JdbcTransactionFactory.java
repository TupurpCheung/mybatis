package cn.tupurpcheung.mybatis.transaction.jdbc;

import cn.tupurpcheung.mybatis.transaction.Transaction;
import cn.tupurpcheung.mybatis.transaction.TransactionFactory;
import cn.tupurpcheung.mybatis.transaction.TransactionIsolationLevel;

import javax.sql.DataSource;
import java.sql.Connection;

public class JdbcTransactionFactory implements TransactionFactory {
    @Override
    public Transaction newTransaction(Connection conn) {
        return new JdbcTransaction(conn);
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new JdbcTransaction(dataSource, level, autoCommit);
    }
}
