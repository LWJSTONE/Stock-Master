package com.graduation.inventory.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.graduation.inventory.system.entity.SysUser;

import java.util.List;

/**
 * 用户服务接口
 * 
 * @author graduation
 * @version 1.0.0
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 分页查询用户列表
     *
     * @param user 用户查询条件
     * @return 用户列表
     */
    List<SysUser> selectUserList(SysUser user);

    /**
     * 根据ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象
     */
    SysUser selectUserById(Long userId);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    SysUser selectUserByUsername(String username);

    /**
     * 新增用户
     *
     * @param user 用户信息
     * @return 影响行数
     */
    int insertUser(SysUser user);

    /**
     * 修改用户
     *
     * @param user 用户信息
     * @return 影响行数
     */
    int updateUser(SysUser user);

    /**
     * 批量删除用户
     *
     * @param userIds 用户ID数组
     * @return 影响行数
     */
    int deleteUserByIds(Long[] userIds);

    /**
     * 重置密码
     *
     * @param user 用户信息（包含新密码）
     * @return 影响行数
     */
    int resetPwd(SysUser user);

    /**
     * 修改用户状态
     *
     * @param user 用户信息（包含新状态）
     * @return 影响行数
     */
    int updateStatus(SysUser user);

    /**
     * 校验用户名称是否唯一
     *
     * @param user 用户信息
     * @return 结果（true唯一 false不唯一）
     */
    boolean checkUsernameUnique(SysUser user);

    /**
     * 根据用户ID查询用户详情（包含角色ID列表）
     *
     * @param userId 用户ID
     * @return 用户对象（包含roleIds）
     */
    SysUser selectUserWithRoleIds(Long userId);

    /**
     * 根据用户ID查询角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> selectRoleIdsByUserId(Long userId);
}
