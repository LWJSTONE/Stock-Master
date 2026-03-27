package com.graduation.inventory.security;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 权限服务
 * 用于Spring Security权限注解支持
 * 
 * 使用示例：
 * @PreAuthorize("@ss.hasPermi('system:user:list')")
 * @PreAuthorize("@ss.hasAnyPermi('system:user:list', 'system:user:add')")
 * @PreAuthorize("@ss.hasRole('admin')")
 * 
 * @author graduation
 * @version 1.0.0
 */
@Component("ss")
public class PermissionService {

    /**
     * 验证用户是否具有某权限
     * 
     * @param permission 权限标识
     * @return 是否具有权限
     */
    public boolean hasPermi(String permission) {
        if (StrUtil.isBlank(permission)) {
            return false;
        }
        
        LoginUser loginUser = getLoginUser();
        if (loginUser == null) {
            return false;
        }
        
        // 管理员拥有所有权限
        if (isAdmin(loginUser)) {
            return true;
        }
        
        Set<String> permissions = loginUser.getPermissions();
        return permissions != null && permissions.contains(permission);
    }

    /**
     * 验证用户是否不具有某权限
     * 
     * @param permission 权限标识
     * @return 是否不具有权限
     */
    public boolean lacksPermi(String permission) {
        return !hasPermi(permission);
    }

    /**
     * 验证用户是否具有任意一个权限
     * 
     * @param permissions 权限标识列表（逗号分隔）
     * @return 是否具有任意一个权限
     */
    public boolean hasAnyPermi(String permissions) {
        if (StrUtil.isBlank(permissions)) {
            return false;
        }
        
        LoginUser loginUser = getLoginUser();
        if (loginUser == null) {
            return false;
        }
        
        // 管理员拥有所有权限
        if (isAdmin(loginUser)) {
            return true;
        }
        
        Set<String> userPermissions = loginUser.getPermissions();
        if (userPermissions == null || userPermissions.isEmpty()) {
            return false;
        }
        
        String[] permArray = permissions.split(",");
        for (String perm : permArray) {
            String trimmedPerm = perm.trim();
            if (StringUtils.isNotBlank(trimmedPerm) && userPermissions.contains(trimmedPerm)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * 验证用户是否具有某角色
     * 
     * @param role 角色标识
     * @return 是否具有角色
     */
    public boolean hasRole(String role) {
        if (StrUtil.isBlank(role)) {
            return false;
        }
        
        LoginUser loginUser = getLoginUser();
        if (loginUser == null) {
            return false;
        }
        
        // 管理员拥有所有角色
        if (isAdmin(loginUser)) {
            return true;
        }
        
        Set<String> roles = loginUser.getRoles();
        return roles != null && roles.contains(role);
    }

    /**
     * 验证用户是否具有任意一个角色
     * 
     * @param roles 角色标识列表（逗号分隔）
     * @return 是否具有任意一个角色
     */
    public boolean hasAnyRoles(String roles) {
        if (StrUtil.isBlank(roles)) {
            return false;
        }
        
        LoginUser loginUser = getLoginUser();
        if (loginUser == null) {
            return false;
        }
        
        // 管理员拥有所有角色
        if (isAdmin(loginUser)) {
            return true;
        }
        
        Set<String> userRoles = loginUser.getRoles();
        if (userRoles == null || userRoles.isEmpty()) {
            return false;
        }
        
        String[] roleArray = roles.split(",");
        for (String role : roleArray) {
            String trimmedRole = role.trim();
            if (StringUtils.isNotBlank(trimmedRole) && userRoles.contains(trimmedRole)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * 判断用户是否为管理员
     * 
     * @param loginUser 登录用户
     * @return 是否为管理员
     */
    private boolean isAdmin(LoginUser loginUser) {
        // 判断是否为超级管理员（userId为1）
        return loginUser.getUserId() != null && loginUser.getUserId() == 1L;
    }

    /**
     * 获取当前登录用户
     * 
     * @return 登录用户
     */
    private LoginUser getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUser) {
            return (LoginUser) principal;
        }
        
        return null;
    }

}
