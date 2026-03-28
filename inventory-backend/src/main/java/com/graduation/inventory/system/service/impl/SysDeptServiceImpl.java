package com.graduation.inventory.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.inventory.common.exception.ServiceException;
import com.graduation.inventory.common.utils.StringUtils;
import com.graduation.inventory.system.entity.SysDept;
import com.graduation.inventory.system.entity.SysUser;
import com.graduation.inventory.system.mapper.SysDeptMapper;
import com.graduation.inventory.system.mapper.SysUserMapper;
import com.graduation.inventory.system.service.SysDeptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 部门服务实现
 * 
 * @author graduation
 * @version 1.0.0
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {

    private static final Logger log = LoggerFactory.getLogger(SysDeptServiceImpl.class);

    /**
     * 部门根节点ID
     */
    private static final Long ROOT_DEPT_ID = 0L;

    @Autowired
    private SysDeptMapper deptMapper;

    @Autowired
    private SysUserMapper userMapper;

    /**
     * 查询部门列表
     *
     * @param dept 部门查询条件
     * @return 部门列表
     */
    @Override
    public List<SysDept> selectDeptList(SysDept dept) {
        LambdaQueryWrapper<SysDept> queryWrapper = new LambdaQueryWrapper<>();

        // 根据部门名称模糊查询
        if (StringUtils.isNotBlank(dept.getDeptName())) {
            queryWrapper.like(SysDept::getDeptName, dept.getDeptName());
        }
        // 根据状态精确查询
        if (StringUtils.isNotBlank(dept.getStatus())) {
            queryWrapper.eq(SysDept::getStatus, dept.getStatus());
        }
        // 排除已删除部门
        queryWrapper.eq(SysDept::getDelFlag, "0");
        // 按父ID和排序号排序
        queryWrapper.orderByAsc(SysDept::getParentId, SysDept::getOrderNum);

        return deptMapper.selectList(queryWrapper);
    }

    /**
     * 根据ID查询部门
     *
     * @param deptId 部门ID
     * @return 部门对象
     */
    @Override
    public SysDept selectDeptById(Long deptId) {
        if (deptId == null) {
            return null;
        }
        return deptMapper.selectById(deptId);
    }

    /**
     * 构建部门树
     *
     * @param depts 部门列表
     * @return 部门树
     */
    @Override
    public List<SysDept> buildDeptTree(List<SysDept> depts) {
        if (depts == null || depts.isEmpty()) {
            return new ArrayList<>();
        }

        // 构建树形结构
        return buildTree(depts, ROOT_DEPT_ID);
    }

    /**
     * 新增部门
     *
     * @param dept 部门信息
     * @return 影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDept(SysDept dept) {
        // 校验部门名称唯一性
        if (!checkDeptNameUnique(dept)) {
            throw new ServiceException("同一级别下部门名称已存在");
        }

        // 检查父部门状态
        if (dept.getParentId() != null && !ROOT_DEPT_ID.equals(dept.getParentId())) {
            SysDept parentDept = deptMapper.selectById(dept.getParentId());
            if (parentDept == null) {
                throw new ServiceException("父部门不存在");
            }
            if ("1".equals(parentDept.getStatus())) {
                throw new ServiceException("父部门已停用，不允许新增");
            }
        }

        // 默认状态为正常
        if (StringUtils.isBlank(dept.getStatus())) {
            dept.setStatus("0");
        }

        // 设置删除标志
        dept.setDelFlag("0");

        int result = deptMapper.insert(dept);
        log.info("新增部门成功, 部门名称: {}, 部门ID: {}", dept.getDeptName(), dept.getId());

        return result;
    }

    /**
     * 修改部门
     *
     * @param dept 部门信息
     * @return 影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDept(SysDept dept) {
        if (dept.getId() == null) {
            throw new ServiceException("部门ID不能为空");
        }

        // 校验部门名称唯一性（同级下）
        if (StringUtils.isNotBlank(dept.getDeptName())) {
            Long parentId = dept.getParentId() != null ? dept.getParentId() : ROOT_DEPT_ID;
            LambdaQueryWrapper<SysDept> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysDept::getDeptName, dept.getDeptName());
            queryWrapper.eq(SysDept::getParentId, parentId);
            queryWrapper.eq(SysDept::getDelFlag, "0");
            queryWrapper.ne(SysDept::getId, dept.getId());
            if (deptMapper.selectCount(queryWrapper) > 0) {
                throw new ServiceException("同一级别下部门名称已存在");
            }
        }

        // 不能将父部门设置为自己或自己的子部门
        if (dept.getParentId() != null && dept.getParentId().equals(dept.getId())) {
            throw new ServiceException("上级部门不能选择自己");
        }

        // 检查父部门状态
        if (dept.getParentId() != null && !ROOT_DEPT_ID.equals(dept.getParentId())) {
            SysDept parentDept = deptMapper.selectById(dept.getParentId());
            if (parentDept == null) {
                throw new ServiceException("父部门不存在");
            }
            if ("1".equals(parentDept.getStatus())) {
                throw new ServiceException("父部门已停用，不允许修改");
            }
        }

        int result = deptMapper.updateById(dept);
        log.info("修改部门成功, 部门ID: {}", dept.getId());

        return result;
    }

    /**
     * 删除部门
     *
     * @param deptId 部门ID
     * @return 影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDeptById(Long deptId) {
        if (deptId == null) {
            throw new ServiceException("部门ID不能为空");
        }

        // 检查是否存在子部门
        if (hasChildByDeptId(deptId)) {
            throw new ServiceException("存在子部门，不允许删除");
        }

        // 检查部门是否存在用户
        if (checkDeptExistUser(deptId)) {
            throw new ServiceException("部门存在用户，不允许删除");
        }

        // 逻辑删除
        SysDept dept = new SysDept();
        dept.setId(deptId);
        dept.setDelFlag("1");

        int result = deptMapper.updateById(dept);
        log.info("删除部门成功, 部门ID: {}", deptId);

        return result;
    }

    /**
     * 校验部门名称是否唯一
     *
     * @param dept 部门信息
     * @return 结果（true唯一 false不唯一）
     */
    @Override
    public boolean checkDeptNameUnique(SysDept dept) {
        if (StringUtils.isBlank(dept.getDeptName())) {
            return false;
        }

        Long parentId = dept.getParentId() != null ? dept.getParentId() : ROOT_DEPT_ID;

        LambdaQueryWrapper<SysDept> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDept::getDeptName, dept.getDeptName());
        queryWrapper.eq(SysDept::getParentId, parentId);
        queryWrapper.eq(SysDept::getDelFlag, "0");

        SysDept existingDept = deptMapper.selectOne(queryWrapper);
        if (existingDept == null) {
            return true;
        }
        // 修改时，排除自己
        if (dept.getId() != null && existingDept.getId().equals(dept.getId())) {
            return true;
        }

        return false;
    }

    /**
     * 是否存在子节点
     *
     * @param deptId 部门ID
     * @return 结果（true存在 false不存在）
     */
    @Override
    public boolean hasChildByDeptId(Long deptId) {
        LambdaQueryWrapper<SysDept> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDept::getParentId, deptId);
        queryWrapper.eq(SysDept::getDelFlag, "0");
        return deptMapper.selectCount(queryWrapper) > 0;
    }

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果（true存在 false不存在）
     */
    @Override
    public boolean checkDeptExistUser(Long deptId) {
        List<SysUser> users = userMapper.selectUsersByDeptId(deptId);
        return users != null && !users.isEmpty();
    }

    /**
     * 构建树形结构
     *
     * @param depts    部门列表
     * @param parentId 父ID
     * @return 树形结构
     */
    private List<SysDept> buildTree(List<SysDept> depts, Long parentId) {
        List<SysDept> returnList = new ArrayList<>();
        for (Iterator<SysDept> iterator = depts.iterator(); iterator.hasNext(); ) {
            SysDept dept = iterator.next();
            if (parentId.equals(dept.getParentId())) {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        return returnList;
    }

    /**
     * 递归列表
     *
     * @param list 部门列表
     * @param dept 当前部门
     */
    private void recursionFn(List<SysDept> list, SysDept dept) {
        // 获取子节点列表
        List<SysDept> childList = getChildList(list, dept);
        dept.setChildren(childList);
        for (SysDept child : childList) {
            if (hasChild(list, child)) {
                recursionFn(list, child);
            }
        }
    }

    /**
     * 得到子节点列表
     *
     * @param list 部门列表
     * @param dept 当前部门
     * @return 子节点列表
     */
    private List<SysDept> getChildList(List<SysDept> list, SysDept dept) {
        List<SysDept> childList = new ArrayList<>();
        for (SysDept item : list) {
            if (dept.getId().equals(item.getParentId())) {
                childList.add(item);
            }
        }
        return childList;
    }

    /**
     * 判断是否有子节点
     *
     * @param list 部门列表
     * @param dept 当前部门
     * @return 是否有子节点
     */
    private boolean hasChild(List<SysDept> list, SysDept dept) {
        return getChildList(list, dept).size() > 0;
    }
}
