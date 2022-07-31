package com.sz.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sz.Mapper.UserInfoMapper;
import com.sz.Service.UserInfoService;
import com.sz.commonutils.Helper.JWTHelper;
import com.sz.model.User.UserInfo;
import com.sz.model.VO.user.LoginVo;
import com.sz.model.VO.user.UserInfoQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        String phone = loginVo.getPhone();
        String veriCode = loginVo.getCode();
        if(phone == null || veriCode == null) return null;

        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        UserInfo userInfo = baseMapper.selectOne(queryWrapper);

        if(userInfo == null){
            userInfo = new UserInfo();
            userInfo.setStatus(1);
            userInfo.setPhone(phone);
            userInfo.setDeleted(0);
            userInfo.setName("");
            userInfo.setNickName("");
            userInfo.setCreateTime(new Timestamp(System.currentTimeMillis()));
            userInfo.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            baseMapper.insert(userInfo);
        } else if(userInfo.getStatus() == 0){
            return null;
        }

        Map<String, Object> ret = new HashMap<>();

        String token = JWTHelper.createToken(userInfo.getId(), phone);

        ret.put("phone", phone);
        ret.put("token", token);

        return ret;
    }

    @Override
    public IPage<UserInfo> selectPage(Page<UserInfo> page, UserInfoQueryVo userInfoVo) {
        QueryWrapper<UserInfo> query = new QueryWrapper<>();
        if(userInfoVo.getKeyword() != null){
            query.like("name", userInfoVo.getKeyword());
        }

        if(userInfoVo.getStatus() != null){
            query.like("status", userInfoVo.getStatus());
        }

        if(userInfoVo.getAuthStatus() != null){
            query.like("auth_status", userInfoVo.getAuthStatus());
        }

        if(userInfoVo.getCreateTimeBegin() != null){
            query.ge("create_time", userInfoVo.getCreateTimeBegin());
        }

        if(userInfoVo.getCreateTimeEnd() != null){
            query.le("create_time", userInfoVo.getCreateTimeEnd());
        }
        return baseMapper.selectPage(page, query);
    }

    @Override
    public Boolean lock(Long userId, Integer status) {
        if(status != 0 && status != 1) return false;
        UserInfo userInfo = baseMapper.selectById(userId);
        userInfo.setStatus(status);
        baseMapper.updateById(userInfo);
        return true;
    }

}
