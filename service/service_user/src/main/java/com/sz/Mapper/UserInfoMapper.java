package com.sz.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sz.model.User.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
}
