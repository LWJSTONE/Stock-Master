package com.graduation.inventory.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.inventory.common.exception.ServiceException;
import com.graduation.inventory.common.utils.StringUtils;
import com.graduation.inventory.system.entity.SysRole;
import com.graduation.inventory.system.entity.SysRoleMenu;
import com.graduation.inventory.system.mapper.SysRoleMapper;
import com.graduation.inventory.system.mapper.SysRoleMenuMapper;
import com.graduation.inventory.system.service.SysRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 角色服务实现
 * 
 * @author graduation
 * @version 1.0.0
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private static final Logger log = LoggerFactory.getLogger(SysRoleServiceImpl.class);

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    /**
     * 分页查询角色列表
     *
     * @param role 角色查询条件
     * @return 角色列表
     */
    @Override
    public List<SysRole> selectRoleList(SysRole role) {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();

        // 根据角色名称模糊查询
        if (StringUtils.isNotBlank(role.getRoleName())) {
            queryWrapper.like(SysRole::getRoleName, role.getRoleName());
        }
        // 根据角色权限字符模糊查询
        if (StringUtils.isNotBlank(role.getRoleKey())) {
            queryWrapper.like(SysRole::getRoleKey, role.getRoleKey());
        }
        // 根据状态精确查询
        if (StringUtils.isNotBlank(role.getStatus())) {
            queryWrapper.eq(SysRole::getStatus, role.getStatus());
        }
        // 排除已删除角色
        queryWrapper.eq(SysRole::getDelFlag, "0");
        // 按创建时间倒序
        queryWrapper.orderByDesc(SysRole::getCreateTime);

        return roleMapper.selectList(queryWrapper);
    }

    /**
     * 根据ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象
     */
    @Override
    public SysRole selectRoleById(Long roleId) {
        if (roleId == null) {
            return null;
        }
        return roleMapper.selectById(roleId);
    }

    /**
     * 新增角色
     *
     * @param role 角色信息
     * @return 影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRole(SysRole role) {
        // 校验角色名称唯一性
        if (!checkRoleNameUnique(role)) {
            throw new ServiceException("角色名称已存在");
        }
        // 校验角色权限字符唯一性
        if (!checkRoleKeyUnique(role)) {
            throw new ServiceException("角色权限字符已存在");
        }

        // 默认状态为正常
        if (StringUtils.isBlank(role.getStatus())) {
            role.setStatus("0");
        }

        // 设置删除标志
        role.setDelFlag("0");

        int result = roleMapper.insert(role);
        log.info("新增角色成功, 角色名称: {}, 角色ID: {}", role.getRoleName(), role.getId());

        return result;
    }

    /**
     * 修改角色
     *
     * @param role 角色信息
     * @return 影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRole(SysRole role) {
        if (role.getId() == null) {
            throw new ServiceException("角色ID不能为空");
        }

        // 校验角色名称唯一性（修改时排除自己）
        if (StringUtils.isNotBlank(role.getRoleName())) {
            LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysRole::getRoleName, role.getRoleName());
            queryWrapper.ne(SysRole::getId, role.getId());
            queryWrapper.eq(SysRole::getDelFlag, "0");
            if (roleMapper.selectCount(queryWrapper) > 0) {
                throw new ServiceException("角色名称已存在");
            }
        }

        // 校验角色权限字符唯一性（修改时排除自己）
        if (StringUtils.isNotBlank(role.getRoleKey())) {
            LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysRole::getRoleKey, role.getRoleKey());
            queryWrapper.ne(SysRole::getId, role.getId());
            queryWrapper.eq(SysRole::getDelFlag, "0");
            if (roleMapper.selectCount(queryWrapper) > 0) {
                throw new ServiceException("角色权限字符已存在");
            }
        }

        int result = roleMapper.updateById(role);
        log.info("修改角色成功, 角色ID: {}", role.getId());

        return result;
    }

    /**
     * 批量删除角色
     *
     * @param roleIds 角色ID数组
     * @return 影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRoleByIds(Long[] roleIds) {
        if (roleIds == null || roleIds.length == 0) {
            throw new ServiceException("角色ID不能为空");
        }

        // 逻辑删除角色
        int result = 0;
        for (Long roleId : roleIds) {
            SysRole role = new SysRole();
            role.setId(roleId);
            role.setDelFlag("1");
            result += roleMapper.updateById(role);
        }

        // 删除角色菜单关联
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysRoleMenu::getRoleId, Arrays.asList(roleIds));
        roleMenuMapper.delete(queryWrapper);

        log.info("批量删除角色成功, 角色ID: {}", Arrays.toString(roleIds));

        return result;
    }

    /**
     * 分配菜单权限
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID数组
     * @return 影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int assignMenu(Long roleId, Long[] menuIds) {
        if (roleId == null) {
            throw new ServiceException("角色ID不能为空");
        }

        // 先删除角色原有的菜单关联
        LambdaQueryWrapper<SysRoleMenu> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(SysRoleMenu::getRoleId, roleId);
        roleMenuMapper.delete(deleteWrapper);

        // 新增角色菜单关联
        int result = 0;
        if (menuIds != null && menuIds.length > 0) {
            for (Long menuId : menuIds) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                roleMenuMapper.insert(roleMenu);
                result++;
            }
        }

        log.info("分配菜单权限成功, 角色ID: {}, 菜单数量: {}", roleId, result);

        return result;
    }

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果（true唯一 false不唯一）
     */
    @Override
    public boolean checkRoleNameUnique(SysRole role) {
        if (StringUtils.isBlank(role.getRoleName())) {
            return false;
        }

        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getRoleName, role.getRoleName());
        queryWrapper.eq(SysRole::getDelFlag, "0");

        SysRole existingRole = roleMapper.selectOne(queryWrapper);
        if (existingRole == null) {
            return true;
        }
        // 修改时，排除自己
        if (role.getId() != null && existingRole.getId().equals(role.getId())) {
            return true;
        }

        return false;
    }

    /**
     * 校验角色权限字符是否唯一
     *
     * @param role 角色信息
     * @return 结果（true唯一 false不唯一）
     */
    @Override
    public boolean checkRoleKeyUnique(SysRole role) {
        if (StringUtils.isBlank(role.getRoleKey())) {
            return false;
        }

        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getRoleKey, role.getRoleKey());
        queryWrapper.eq(SysRole::getDelFlag, "0");

        SysRole existingRole = roleMapper.selectOne(queryWrapper);
        if (existingRole == null) {
            return true;
        }
        // 修改时，排除自己
        if (role.getId() != null && existingRole.getId().equals(role.getId())) {
            return true;
        }

        return false;
    }

    /**
     * 修改角色状态
     *
     * @param role 角色信息（包含新状态）
     * @return 影响行数
     */
    @Override
    public int updateStatus(SysRole role) {
        if (role.getId() == null) {
            throw new ServiceException("角色ID不能为空");
        }
        if (StringUtils.isBlank(role.getStatus())) {
            throw new ServiceException("状态不能为空");
        }

        // 只更新状态字段
        SysRole updateRole = new SysRole();
        updateRole.setId(role.getId());
        updateRole.setStatus(role.getStatus());

        int result = roleMapper.updateById(updateRole);
        log.info("修改角色状态成功, 角色ID: {}, 状态: {}", role.getId(), role.getStatus());

        return result;
    }

    /**
     * 根据角色ID查询菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    @Override
    public List<Long> selectMenuIdsByRoleId(Long roleId) {
        if (roleId == null) {
            return new java.util.ArrayList<>();
        }
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(queryWrapper);
        return roleMenus.stream().map(SysRoleMenu::getMenuId).collect(java.util.stream.Collectors.toList());
    }
}
