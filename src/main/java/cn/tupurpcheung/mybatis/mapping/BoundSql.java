package cn.tupurpcheung.mybatis.mapping;

import java.util.Map;
/**
 * 绑定的SQL,是从 xml 而来，将动态内容都处理完成得到的SQL语句字符串，其中包括?,还有绑定的参
 * */
public class BoundSql {

    private String sql;
    private String parameterType;
    private String resultType;
    private Map<Integer, String> parameterMappings;

    public BoundSql(String sql,Map<Integer, String> parameterMappings,String parameterType,String resultType){
        this.sql = sql;
        this.parameterMappings = parameterMappings;
        this.parameterType = parameterType;
        this.resultType = resultType;
    }


    public String getSql() {


        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public Map<Integer, String> getParameterMappings() {
        return parameterMappings;
    }

    public void setParameterMappings(Map<Integer, String> parameterMappings) {
        this.parameterMappings = parameterMappings;
    }
}
