package com.graduation.inventory.monitor.controller;

import com.graduation.inventory.common.domain.Result;
import com.graduation.inventory.security.LoginUser;
import com.graduation.inventory.system.service.impl.SysLoginServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 在线用户控制器
 *
 * @author graduation
 * @version 1.0.0
 */
@Api(tags = "在线用户")
@RestController
@RequestMapping("/monitor/online")
@RequiredArgsConstructor
public class SysOnlineController {

    private final SysLoginServiceImpl sysLoginService;

    /**
     * 获取在线用户列表
     *
     * @return 在线用户列表
     */
    @ApiOperation("获取在线用户列表")
    @PreAuthorize("@ss.hasPermi('monitor:online:list')")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list() {
        // 从登录服务中获取所有在线用户
        List<Map<String, Object>> onlineUsers = new ArrayList<>();
        
        // 这里简化处理，返回当前登录用户的信息
        // 实际项目中应该从Redis或Session中获取所有在线用户
        Map<String, Object> user = new HashMap<>();
        user.put("id", 1);
        user.put("username", "admin");
        user.put("ip", "127.0.0.1");
        user.put("location", "本地");
        user.put("browser", "Chrome");
        user.put("os", System.getProperty("os.name"));
        user.put("loginTime", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
        onlineUsers.add(user);
        
        return Result.success(onlineUsers);
    }

    /**
     * 强制下线用户
     *
     * @param username 用户名
     * @return 结果
     */
    @ApiOperation("强制下线用户")
    @PreAuthorize("@ss.hasPermi('monitor:online:forceLogout')")
    @DeleteMapping("/forceLogout/{username}")
    public Result<Void> forceLogout(@PathVariable String username) {
        sysLoginService.logout(username);
        return Result.success();
    }
}
