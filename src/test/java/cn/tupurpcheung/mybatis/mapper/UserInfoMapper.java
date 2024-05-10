package cn.tupurpcheung.mybatis.mapper;

import cn.tupurpcheung.mybatis.po.UserInfo;

public interface UserInfoMapper {
    UserInfo queryUserById(int uId);
}
