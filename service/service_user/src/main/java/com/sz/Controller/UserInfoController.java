package com.sz.Controller;

import com.sz.Service.Impl.UserInfoServiceImpl;
import com.sz.commonutils.Result.Result;
import com.sz.model.VO.user.LoginVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserInfoController {
    @Autowired
    private UserInfoServiceImpl userInfoService;

    @ApiOperation("手机号验证码登陆")
    @PostMapping("/login")
    public Result login(LoginVo loginVo){
        Map<String, Object> map = userInfoService.login(loginVo);
        return Result.ok(map);
    }
}
