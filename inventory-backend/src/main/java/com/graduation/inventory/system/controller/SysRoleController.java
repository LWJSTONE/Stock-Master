package com.graduation.inventory.system.controller;

import com.graduation.inventory.common.annotation.Log;
import com.graduation.inventory.common.domain.PageResult;
import com.graduation.inventory.common.domain.Result;
import com.graduation.inventory.common.enums.BusinessType;
import com.graduation.inventory.system.entity.SysRole;
import com.graduation.inventory.system.entity.dto.AllotMenuDto;
import com.graduation.inventory.system.service.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 角色控制器
 *
 * @author graduation
 * @version 1.0.0
 */
@Api(tags = "角色管理")
@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;

    /**
     * 分页查询角色列表
     *
     * @param pageNum   页码
     * @param pageSize  每页数量
     * @param roleName  角色名称
     * @param status    状态
     * @return 角色列表
     */
    @ApiOperation("分页查询角色列表")
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/list")
    public Result<PageResult<SysRole>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("角色名称") @RequestParam(required = false) String roleName,
            @ApiParam("状态") @RequestParam(required = false) String status) {
        SysRole role = new SysRole();
        role.setRoleName(roleName);
        role.setStatus(status);
        List<SysRole> list = sysRoleService.selectRoleList(role);
        // 手动分页
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, list.size());
        List<SysRole> pageList = list.subList(start, end);
        return Result.success(new PageResult<>(pageList, (long) list.size()));
    }

    /**
     * 获取角色详情
     *
     * @param roleId 角色ID
     * @return 角色详情
     */
    @ApiOperation("获取角色详情")
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping("/{roleId}")
    public Result<SysRole> getInfo(@ApiParam("角色ID") @PathVariable Long roleId) {
        return Result.success(sysRoleService.getById(roleId));
    }

    /**
     * 新增角色
     *
     * @param role 角色信息
     * @return 结果
     */
    @ApiOperation("新增角色")
    @PreAuthorize("@ss.hasPermi('system:role:add')")
    @Log(title = "角色管理", action = BusinessType.INSERT)
    @PostMapping
    public Result<Void> add(@Validated @RequestBody SysRole role) {
        if (!sysRoleService.checkRoleNameUnique(role)) {
            return Result.error("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        if (!sysRoleService.checkRoleKeyUnique(role)) {
            return Result.error("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        return sysRoleService.save(role) ? Result.success() : Result.error("新增角色失败");
    }

    /**
     * 修改角色
     *
     * @param role 角色信息
     * @return 结果
     */
    @ApiOperation("修改角色")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", action = BusinessType.UPDATE)
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody SysRole role) {
        if (!sysRoleService.checkRoleNameUnique(role)) {
            return Result.error("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        if (!sysRoleService.checkRoleKeyUnique(role)) {
            return Result.error("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        return sysRoleService.updateById(role) ? Result.success() : Result.error("修改角色失败");
    }

    /**
     * 删除角色
     *
     * @param roleIds 角色ID列表
     * @return 结果
     */
    @ApiOperation("删除角色")
    @PreAuthorize("@ss.hasPermi('system:role:remove')")
    @Log(title = "角色管理", action = BusinessType.DELETE)
    @DeleteMapping("/{roleIds}")
    public Result<Void> remove(@ApiParam("角色ID列表") @PathVariable Long[] roleIds) {
        return sysRoleService.removeByIds(Arrays.asList(roleIds)) ? Result.success() : Result.error("删除角色失败");
    }

    /**
     * 分配菜单权限
     *
     * @param allotMenuDto 分配菜单请求
     * @return 结果
     */
    @ApiOperation("分配菜单权限")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", action = BusinessType.GRANT)
    @PutMapping("/allotMenu")
    public Result<Void> allotMenu(@Validated @RequestBody AllotMenuDto allotMenuDto) {
        int result = sysRoleService.assignMenu(allotMenuDto.getRoleId(), allotMenuDto.getMenuIds().toArray(new Long[0]));
        return result > 0 ? Result.success() : Result.error("分配菜单权限失败");
    }
}
