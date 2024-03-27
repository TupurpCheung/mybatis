package cn.tupurpcheung.mybatis.version1.mapper;

public interface UserMapper {
    String queryUserName(String uId);

    Integer queryUserAge(String uId);
}
