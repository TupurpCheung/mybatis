package cn.tupurpcheung.mybatis.session.impl;

import cn.tupurpcheung.mybatis.executor.Executor;
import cn.tupurpcheung.mybatis.mapping.Environment;
import cn.tupurpcheung.mybatis.session.Configuration;
import cn.tupurpcheung.mybatis.session.SqlSession;
import cn.tupurpcheung.mybatis.session.SqlSessionFactory;
import cn.tupurpcheung.mybatis.transaction.Transaction;
import cn.tupurpcheung.mybatis.transaction.TransactionFactory;
import cn.tupurpcheung.mybatis.transaction.TransactionIsolationLevel;

import java.sql.SQLException;

/**
 * 创建session
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {


    private Configuration configuration;


    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {

        Transaction tx = null;
        try {
            final Environment environment = configuration.getEnvironment();
            TransactionFactory transactionFactory = environment.getTransactionFactory();
            tx = transactionFactory.newTransaction(configuration.getEnvironment().getDataSource(), TransactionIsolationLevel.READ_COMMITTED, false);
            // 创建执行器
            final Executor executor = configuration.newExecutor(tx);
            // 创建DefaultSqlSession
            return new DefaultSqlSession(configuration, executor);
        } catch (Exception e) {
            try {
                assert tx != null;
                tx.close();
            } catch (SQLException ignore) {
            }
            throw new RuntimeException("Error opening session.  Cause: " + e);
        }


    }
}
