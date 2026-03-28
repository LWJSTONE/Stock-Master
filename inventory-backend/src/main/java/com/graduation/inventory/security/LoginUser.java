package com.graduation.inventory.security;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 登录用户身份封装
 * 实现Spring Security的UserDetails接口
 * 
 * @author graduation
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
public class LoginUser implements UserDetails {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 权限列表
     */
    private Set<String> permissions;

    /**
     * 角色列表
     */
    private Set<String> roles;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * Token
     */
    private String token;

    /**
     * 用户状态（0正常 1停用）
     */
    private String status;

    /**
     * 删除标志（0存在 1删除）
     */
    private String delFlag;

    /**
     * 构造函数
     * 
     * @param userId      用户ID
     * @param username    用户名
     * @param password    密码
     * @param deptId      部门ID
     * @param permissions 权限列表
     */
    public LoginUser(Long userId, String username, String password, Long deptId, Set<String> permissions) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.deptId = deptId;
        this.permissions = permissions != null ? permissions : new HashSet<>();
    }

    /**
     * 获取用户权限集合
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        // 添加权限
        if (permissions != null && !permissions.isEmpty()) {
            authorities.addAll(permissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet()));
        }
        // 添加角色（以ROLE_开头）
        if (roles != null && !roles.isEmpty()) {
            authorities.addAll(roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toSet()));
        }
        return authorities;
    }

    /**
     * 获取密码
     */
    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * 获取用户名
     */
    @Override
    public String getUsername() {
        return this.username;
    }

    /**
     * 账户是否未过期
     * 默认返回true（未过期）
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 账户是否未锁定
     * 默认返回true（未锁定）
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 密码是否未过期
     * 默认返回true（未过期）
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 账户是否可用
     * 根据用户状态判断
     */
    @Override
    public boolean isEnabled() {
        return !"1".equals(this.status);
    }

}
