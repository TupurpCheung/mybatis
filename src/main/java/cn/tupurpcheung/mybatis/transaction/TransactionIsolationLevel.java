package cn.tupurpcheung.mybatis.transaction;

import static java.sql.Connection.*;

public enum TransactionIsolationLevel {
    //无事务
    NONE(TRANSACTION_NONE, "none"),
    //读未提交
    READ_UNCOMMITTED(TRANSACTION_READ_UNCOMMITTED, "read_uncommitted"),
    //读已提交
    READ_COMMITTED(TRANSACTION_READ_COMMITTED, "read_committed"),
    //可重复读
    REPEATABLE_READ(TRANSACTION_REPEATABLE_READ, "repeatable_read"),
    //序列化读
    SERIALIZABLE(TRANSACTION_SERIALIZABLE, "serializable");


    int level;
    String desc;

    private TransactionIsolationLevel(int level, String desc) {
        this.level = level;
        this.desc = desc;
    }

    public int getLevel() {
        return level;
    }

    public String getDesc() {
        return desc;
    }
}
