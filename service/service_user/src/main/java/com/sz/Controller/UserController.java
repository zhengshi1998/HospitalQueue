package com.sz.Controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sz.Service.Impl.UserInfoServiceImpl;
import com.sz.commonutils.Result.Result;
import com.sz.model.User.UserInfo;
import com.sz.model.VO.user.UserInfoQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/user")
public class UserController {

    @Autowired
    private UserInfoServiceImpl userInfoService;

    @GetMapping("/list/{page}/{limit}")
    public Result list(@PathVariable("page") Integer page, @PathVariable("limit") Integer limit,
                       UserInfoQueryVo userInfoVo){
        Page<UserInfo> pageObj = new Page<>(page, limit);

        IPage<UserInfo> infoIPage = userInfoService.selectPage(pageObj, userInfoVo);
        return Result.ok(infoIPage);
    }

    @GetMapping("/lock/{userId}/{status}")
    public Result lock(@PathVariable("userId") Long userId, @PathVariable("status") Integer status){

        userInfoService.lock(userId, status);
        return Result.ok();
    }
}
