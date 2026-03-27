package com.graduation.inventory.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.graduation.inventory.system.entity.SysRole;

import java.util.List;

/**
 * 角色服务接口
 * 
 * @author graduation
 * @version 1.0.0
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 分页查询角色列表
     *
     * @param role 角色查询条件
     * @return 角色列表
     */
    List<SysRole> selectRoleList(SysRole role);

    /**
     * 根据ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象
     */
    SysRole selectRoleById(Long roleId);

    /**
     * 新增角色
     *
     * @param role 角色信息
     * @return 影响行数
     */
    int insertRole(SysRole role);

    /**
     * 修改角色
     *
     * @param role 角色信息
     * @return 影响行数
     */
    int updateRole(SysRole role);

    /**
     * 批量删除角色
     *
     * @param roleIds 角色ID数组
     * @return 影响行数
     */
    int deleteRoleByIds(Long[] roleIds);

    /**
     * 分配菜单权限
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID数组
     * @return 影响行数
     */
    int assignMenu(Long roleId, Long[] menuIds);

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果（true唯一 false不唯一）
     */
    boolean checkRoleNameUnique(SysRole role);

    /**
     * 校验角色权限字符是否唯一
     *
     * @param role 角色信息
     * @return 结果（true唯一 false不唯一）
     */
    boolean checkRoleKeyUnique(SysRole role);
}
