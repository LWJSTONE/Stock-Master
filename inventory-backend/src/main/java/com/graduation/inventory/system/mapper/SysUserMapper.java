package com.graduation.inventory.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.inventory.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口
 * 
 * @author graduation
 * @version 1.0.0
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    SysUser selectUserByUsername(@Param("username") String username);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户对象
     */
    SysUser selectUserByPhone(@Param("phone") String phone);

    /**
     * 根据部门ID查询用户列表
     *
     * @param deptId 部门ID
     * @return 用户列表
     */
    List<SysUser> selectUsersByDeptId(@Param("deptId") Long deptId);

    /**
     * 根据用户ID查询角色编码列表
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    List<String> selectRoleKeysByUserId(@Param("userId") Long userId);
}
