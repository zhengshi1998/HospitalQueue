package com.sz.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sz.model.User.UserInfo;
import com.sz.model.VO.user.LoginVo;
import com.sz.model.VO.user.UserInfoQueryVo;

import java.util.Map;

public interface UserInfoService {
    Map<String, Object> login(LoginVo loginVo);

    IPage<UserInfo> selectPage(Page<UserInfo> page, UserInfoQueryVo userInfoVo);

    Boolean lock(Long userId, Integer status);
}
