package cn.tupurpcheung.mybatis.executor.resultsets;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public interface ResultHandler {

    <E> List<E> handleResultSets(Statement stmt) throws SQLException;

}
