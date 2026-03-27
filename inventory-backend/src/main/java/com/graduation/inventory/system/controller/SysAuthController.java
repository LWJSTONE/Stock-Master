package com.graduation.inventory.system.controller;

import com.graduation.inventory.common.annotation.Log;
import com.graduation.inventory.common.domain.Result;
import com.graduation.inventory.common.enums.BusinessType;
import com.graduation.inventory.system.entity.dto.LoginDto;
import com.graduation.inventory.system.entity.dto.UserInfoVo;
import com.graduation.inventory.system.entity.vo.RouterVo;
import com.graduation.inventory.system.service.SysAuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证控制器
 *
 * @author graduation
 * @version 1.0.0
 */
@Api(tags = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class SysAuthController {

    private final SysAuthService sysAuthService;

    /**
     * 登录接口
     *
     * @param loginDto 登录请求
     * @return Token
     */
    @ApiOperation("登录")
    @Log(title = "用户登录", action = BusinessType.OTHER)
    @PostMapping("/login")
    public Result<Map<String, String>> login(@Validated @RequestBody LoginDto loginDto) {
        String token = sysAuthService.login(loginDto);
        Map<String, String> data = new HashMap<>(1);
        data.put("token", token);
        return Result.success("登录成功", data);
    }

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @ApiOperation("获取用户信息")
    @GetMapping("/getInfo")
    public Result<UserInfoVo> getInfo() {
        UserInfoVo userInfo = sysAuthService.getInfo();
        return Result.success(userInfo);
    }

    /**
     * 获取当前用户路由
     *
     * @return 路由列表
     */
    @ApiOperation("获取路由信息")
    @GetMapping("/getRouters")
    public Result<List<RouterVo>> getRouters() {
        List<RouterVo> routers = sysAuthService.getRouters();
        return Result.success(routers);
    }

    /**
     * 登出接口
     *
     * @return 成功消息
     */
    @ApiOperation("登出")
    @Log(title = "用户登出", action = BusinessType.OTHER)
    @PostMapping("/logout")
    public Result<Void> logout() {
        sysAuthService.logout();
        return Result.success("退出成功");
    }
}
