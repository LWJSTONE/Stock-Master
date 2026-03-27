package com.graduation.inventory.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.inventory.system.entity.dto.LoginDto;
import com.graduation.inventory.system.entity.dto.UserInfoVo;
import com.graduation.inventory.system.entity.vo.RouterVo;

import java.util.List;

/**
 * 认证服务接口
 *
 * @author graduation
 * @version 1.0.0
 */
public interface SysAuthService {

    /**
     * 登录
     *
     * @param loginDto 登录请求
     * @return Token
     */
    String login(LoginDto loginDto);

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    UserInfoVo getInfo();

    /**
     * 获取当前用户路由
     *
     * @return 路由列表
     */
    List<RouterVo> getRouters();

    /**
     * 登出
     */
    void logout();
}
