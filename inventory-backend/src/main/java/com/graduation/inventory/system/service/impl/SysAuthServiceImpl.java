package com.graduation.inventory.system.service.impl;

import com.graduation.inventory.security.LoginUser;
import com.graduation.inventory.system.entity.SysMenu;
import com.graduation.inventory.system.entity.dto.LoginDto;
import com.graduation.inventory.system.entity.dto.UserInfoVo;
import com.graduation.inventory.system.entity.vo.RouterVo;
import com.graduation.inventory.system.service.SysAuthService;
import com.graduation.inventory.system.service.SysLoginService;
import com.graduation.inventory.system.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 认证服务实现
 *
 * @author graduation
 * @version 1.0.0
 */
@Service
public class SysAuthServiceImpl implements SysAuthService {

    @Autowired
    private SysLoginService sysLoginService;

    @Autowired
    private SysMenuService sysMenuService;

    /**
     * 登录
     *
     * @param loginDto 登录请求
     * @return Token
     */
    @Override
    public String login(LoginDto loginDto) {
        return sysLoginService.login(loginDto.getUsername(), loginDto.getPassword());
    }

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @Override
    public UserInfoVo getInfo() {
        LoginUser loginUser = getLoginUser();
        if (loginUser == null) {
            return null;
        }

        UserInfoVo userInfo = new UserInfoVo();
        userInfo.setUserId(loginUser.getUserId());
        userInfo.setUsername(loginUser.getUsername());
        userInfo.setDeptId(loginUser.getDeptId());
        userInfo.setRoles(loginUser.getRoles() != null ? loginUser.getRoles() : new HashSet<>());
        userInfo.setPermissions(loginUser.getPermissions() != null ? loginUser.getPermissions() : new HashSet<>());

        return userInfo;
    }

    /**
     * 获取当前用户路由
     *
     * @return 路由列表
     */
    @Override
    public List<RouterVo> getRouters() {
        LoginUser loginUser = getLoginUser();
        if (loginUser == null) {
            return null;
        }

        // 根据用户ID查询菜单
        List<SysMenu> menus = sysMenuService.selectMenusByUserId(loginUser.getUserId());

        // 构建路由树
        return sysMenuService.buildRouterTree(menus);
    }

    /**
     * 登出
     */
    @Override
    public void logout() {
        LoginUser loginUser = getLoginUser();
        if (loginUser != null) {
            sysLoginService.logout(loginUser.getUsername());
        }
    }

    /**
     * 获取当前登录用户
     *
     * @return 登录用户
     */
    private LoginUser getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            return (LoginUser) authentication.getPrincipal();
        }
        return null;
    }
}
