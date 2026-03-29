package com.graduation.inventory.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.graduation.inventory.system.entity.SysUser;
import com.graduation.inventory.system.mapper.SysUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 用户详情服务实现
 * 实现Spring Security的UserDetailsService接口
 * 
 * @author graduation
 * @version 1.0.0
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private SysUserMapper userMapper;

    /**
     * 根据用户名加载用户详情
     * 
     * @param username 用户名
     * @return UserDetails 用户详情
     * @throws UsernameNotFoundException 用户名未找到异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("加载用户详情: {}", username);

        // 查询用户信息
        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .eq(SysUser::getDelFlag, "0"));

        if (user == null) {
            log.error("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 检查用户状态
        if ("1".equals(user.getStatus())) {
            log.error("用户已被停用: {}", username);
            throw new UsernameNotFoundException("用户已被停用: " + username);
        }

        // 查询用户权限
        Set<String> permissions = getUserPermissions(user.getId());

        // 查询用户角色
        Set<String> roles = getUserRoles(user.getId());

        // 构建LoginUser对象
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setPassword(user.getPassword());
        loginUser.setDeptId(user.getDeptId());
        loginUser.setPermissions(permissions);
        loginUser.setRoles(roles);
        loginUser.setStatus(user.getStatus());
        loginUser.setDelFlag(user.getDelFlag());

        log.debug("用户 {} 加载成功, 权限数量: {}, 角色数量: {}", 
                username, permissions.size(), roles.size());

        return loginUser;
    }

    /**
     * 获取用户权限列表
     * 
     * @param userId 用户ID
     * @return 权限集合
     */
    private Set<String> getUserPermissions(Long userId) {
        Set<String> permissions = new HashSet<>();
        
        // 从数据库查询用户权限
        List<String> perms = userMapper.selectPermissionsByUserId(userId);
        if (perms != null && !perms.isEmpty()) {
            permissions.addAll(perms);
        }
        
        return permissions;
    }

    /**
     * 获取用户角色列表
     * 
     * @param userId 用户ID
     * @return 角色集合
     */
    private Set<String> getUserRoles(Long userId) {
        Set<String> roles = new HashSet<>();
        
        // 从数据库查询用户角色
        List<String> roleList = userMapper.selectRoleKeysByUserId(userId);
        if (roleList != null && !roleList.isEmpty()) {
            roles.addAll(roleList);
        }
        
        return roles;
    }

}
