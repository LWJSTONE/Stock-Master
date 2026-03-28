package com.graduation.inventory.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.graduation.inventory.system.entity.SysDept;

import java.util.List;

/**
 * 部门服务接口
 * 
 * @author graduation
 * @version 1.0.0
 */
public interface SysDeptService extends IService<SysDept> {

    /**
     * 查询部门列表
     *
     * @param dept 部门查询条件
     * @return 部门列表
     */
    List<SysDept> selectDeptList(SysDept dept);

    /**
     * 根据ID查询部门
     *
     * @param deptId 部门ID
     * @return 部门对象
     */
    SysDept selectDeptById(Long deptId);

    /**
     * 构建部门树
     *
     * @param depts 部门列表
     * @return 部门树
     */
    List<SysDept> buildDeptTree(List<SysDept> depts);

    /**
     * 新增部门
     *
     * @param dept 部门信息
     * @return 影响行数
     */
    int insertDept(SysDept dept);

    /**
     * 修改部门
     *
     * @param dept 部门信息
     * @return 影响行数
     */
    int updateDept(SysDept dept);

    /**
     * 删除部门
     *
     * @param deptId 部门ID
     * @return 影响行数
     */
    int deleteDeptById(Long deptId);

    /**
     * 校验部门名称是否唯一
     *
     * @param dept 部门信息
     * @return 结果（true唯一 false不唯一）
     */
    boolean checkDeptNameUnique(SysDept dept);

    /**
     * 是否存在子节点
     *
     * @param deptId 部门ID
     * @return 结果（true存在 false不存在）
     */
    boolean hasChildByDeptId(Long deptId);

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果（true存在 false不存在）
     */
    boolean checkDeptExistUser(Long deptId);
}
