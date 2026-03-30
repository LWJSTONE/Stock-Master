package com.graduation.inventory.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.inventory.common.exception.ServiceException;
import com.graduation.inventory.common.utils.StringUtils;
import com.graduation.inventory.system.entity.SysDept;
import com.graduation.inventory.system.entity.SysUser;
import com.graduation.inventory.system.entity.SysUserRole;
import com.graduation.inventory.system.mapper.SysDeptMapper;
import com.graduation.inventory.system.mapper.SysUserMapper;
import com.graduation.inventory.system.mapper.SysUserRoleMapper;
import com.graduation.inventory.system.service.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 用户服务实现
 * 
 * @author graduation
 * @version 1.0.0
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysDeptMapper deptMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 分页查询用户列表
     *
     * @param user 用户查询条件
     * @return 用户列表
     */
    @Override
    public List<SysUser> selectUserList(SysUser user) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        
        // 根据用户名模糊查询
        if (StringUtils.isNotBlank(user.getUsername())) {
            queryWrapper.like(SysUser::getUsername, user.getUsername());
        }
        // 根据真实姓名模糊查询
        if (StringUtils.isNotBlank(user.getRealName())) {
            queryWrapper.like(SysUser::getRealName, user.getRealName());
        }
        // 根据手机号模糊查询
        if (StringUtils.isNotBlank(user.getPhone())) {
            queryWrapper.like(SysUser::getPhone, user.getPhone());
        }
        // 根据状态精确查询
        if (StringUtils.isNotBlank(user.getStatus())) {
            queryWrapper.eq(SysUser::getStatus, user.getStatus());
        }
        // 根据部门ID查询
        if (user.getDeptId() != null) {
            queryWrapper.eq(SysUser::getDeptId, user.getDeptId());
        }
        // 排除已删除用户
        queryWrapper.eq(SysUser::getDelFlag, "0");
        // 按创建时间倒序
        queryWrapper.orderByDesc(SysUser::getCreateTime);

        List<SysUser> list = userMapper.selectList(queryWrapper);
        
        // 为每个用户设置部门名称
        for (SysUser sysUser : list) {
            if (sysUser.getDeptId() != null) {
                SysDept dept = deptMapper.selectById(sysUser.getDeptId());
                if (dept != null) {
                    sysUser.setDeptName(dept.getDeptName());
                }
            }
        }
        
        return list;
    }

    /**
     * 根据ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象
     */
    @Override
    public SysUser selectUserById(Long userId) {
        if (userId == null) {
            return null;
        }
        return userMapper.selectById(userId);
    }

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    @Override
    public SysUser selectUserByUsername(String username) {
        if (StringUtils.isBlank(username)) {
            return null;
        }
        return userMapper.selectUserByUsername(username);
    }

    /**
     * 新增用户
     *
     * @param user 用户信息
     * @return 影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertUser(SysUser user) {
        // 校验用户名唯一性
        if (!checkUsernameUnique(user)) {
            throw new ServiceException("用户名已存在");
        }

        // 密码加密
        if (StringUtils.isNotBlank(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // 默认密码
            user.setPassword(passwordEncoder.encode("123456"));
        }

        // 默认状态为正常
        if (StringUtils.isBlank(user.getStatus())) {
            user.setStatus("0");
        }

        // 设置删除标志
        user.setDelFlag("0");

        int result = userMapper.insert(user);
        log.info("新增用户成功, 用户名: {}, 用户ID: {}", user.getUsername(), user.getId());

        // 保存用户角色关联
        if (user.getRoleIds() != null && !user.getRoleIds().isEmpty()) {
            for (Long roleId : user.getRoleIds()) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(user.getId());
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
            log.info("保存用户角色关联成功, 用户ID: {}, 角色IDs: {}", user.getId(), user.getRoleIds());
        }

        return result;
    }

    /**
     * 修改用户
     *
     * @param user 用户信息
     * @return 影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateUser(SysUser user) {
        if (user.getId() == null) {
            throw new ServiceException("用户ID不能为空");
        }

        // 校验用户名唯一性（修改时排除自己）
        if (StringUtils.isNotBlank(user.getUsername())) {
            SysUser existingUser = userMapper.selectUserByUsername(user.getUsername());
            if (existingUser != null && !existingUser.getId().equals(user.getId())) {
                throw new ServiceException("用户名已存在");
            }
        }

        // 不允许修改密码
        user.setPassword(null);

        int result = userMapper.updateById(user);
        log.info("修改用户成功, 用户ID: {}", user.getId());

        // 更新用户角色关联
        if (user.getRoleIds() != null) {
            // 先删除旧的角色关联
            LambdaQueryWrapper<SysUserRole> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(SysUserRole::getUserId, user.getId());
            userRoleMapper.delete(deleteWrapper);
            
            // 再添加新的角色关联
            for (Long roleId : user.getRoleIds()) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(user.getId());
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
            log.info("更新用户角色关联成功, 用户ID: {}, 角色IDs: {}", user.getId(), user.getRoleIds());
        }

        return result;
    }

    /**
     * 批量删除用户
     *
     * @param userIds 用户ID数组
     * @return 影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteUserByIds(Long[] userIds) {
        if (userIds == null || userIds.length == 0) {
            throw new ServiceException("用户ID不能为空");
        }

        // 逻辑删除用户
        LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(SysUser::getId, Arrays.asList(userIds));
        updateWrapper.set(SysUser::getDelFlag, "1");

        int result = userMapper.update(null, updateWrapper);

        // 删除用户角色关联
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysUserRole::getUserId, Arrays.asList(userIds));
        userRoleMapper.delete(queryWrapper);

        log.info("批量删除用户成功, 用户ID: {}", Arrays.toString(userIds));

        return result;
    }

    /**
     * 重置密码
     *
     * @param user 用户信息（包含新密码）
     * @return 影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int resetPwd(SysUser user) {
        if (user.getId() == null) {
            throw new ServiceException("用户ID不能为空");
        }
        if (StringUtils.isBlank(user.getPassword())) {
            throw new ServiceException("密码不能为空");
        }

        // 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        int result = userMapper.updateById(user);
        log.info("重置密码成功, 用户ID: {}", user.getId());

        return result;
    }

    /**
     * 修改用户状态
     *
     * @param user 用户信息（包含新状态）
     * @return 影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateStatus(SysUser user) {
        if (user.getId() == null) {
            throw new ServiceException("用户ID不能为空");
        }
        if (StringUtils.isBlank(user.getStatus())) {
            throw new ServiceException("状态不能为空");
        }

        // 只更新状态字段
        SysUser updateUser = new SysUser();
        updateUser.setId(user.getId());
        updateUser.setStatus(user.getStatus());

        int result = userMapper.updateById(updateUser);
        log.info("修改用户状态成功, 用户ID: {}, 状态: {}", user.getId(), user.getStatus());

        return result;
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param user 用户信息
     * @return 结果（true唯一 false不唯一）
     */
    @Override
    public boolean checkUsernameUnique(SysUser user) {
        if (StringUtils.isBlank(user.getUsername())) {
            return false;
        }

        SysUser existingUser = userMapper.selectUserByUsername(user.getUsername());
        if (existingUser == null) {
            return true;
        }
        // 修改时，排除自己
        if (user.getId() != null && existingUser.getId().equals(user.getId())) {
            return true;
        }

        return false;
    }

    /**
     * 根据用户ID查询用户详情（包含角色ID列表）
     *
     * @param userId 用户ID
     * @return 用户对象（包含roleIds）
     */
    @Override
    public SysUser selectUserWithRoleIds(Long userId) {
        if (userId == null) {
            return null;
        }
        SysUser user = userMapper.selectById(userId);
        if (user != null) {
            // 查询用户的角色ID列表
            List<Long> roleIds = selectRoleIdsByUserId(userId);
            user.setRoleIds(roleIds);
            
            // 设置部门名称
            if (user.getDeptId() != null) {
                SysDept dept = deptMapper.selectById(user.getDeptId());
                if (dept != null) {
                    user.setDeptName(dept.getDeptName());
                }
            }
        }
        return user;
    }

    /**
     * 根据用户ID查询角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    @Override
    public List<Long> selectRoleIdsByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> userRoles = userRoleMapper.selectList(queryWrapper);
        return userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(java.util.stream.Collectors.toList());
    }
}
