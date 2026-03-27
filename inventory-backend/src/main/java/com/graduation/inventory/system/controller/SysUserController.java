package com.graduation.inventory.system.controller;

import com.graduation.inventory.common.annotation.Log;
import com.graduation.inventory.common.domain.PageResult;
import com.graduation.inventory.common.domain.Result;
import com.graduation.inventory.common.enums.BusinessType;
import com.graduation.inventory.system.entity.SysUser;
import com.graduation.inventory.system.entity.dto.ResetPwdDto;
import com.graduation.inventory.system.entity.dto.UserStatusDto;
import com.graduation.inventory.system.service.SysUserService;
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
 * 用户控制器
 *
 * @author graduation
 * @version 1.0.0
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    /**
     * 分页查询用户列表
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @param username 用户名
     * @param status   状态
     * @param deptId   部门ID
     * @return 用户列表
     */
    @ApiOperation("分页查询用户列表")
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public Result<PageResult<SysUser>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("用户名") @RequestParam(required = false) String username,
            @ApiParam("状态") @RequestParam(required = false) String status,
            @ApiParam("部门ID") @RequestParam(required = false) Long deptId) {
        // 构建查询条件
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setStatus(status);
        user.setDeptId(deptId);
        List<SysUser> list = sysUserService.selectUserList(user);
        // 手动分页
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, list.size());
        List<SysUser> pageList = list.subList(start, end);
        return Result.success(new PageResult<>(pageList, (long) list.size()));
    }

    /**
     * 获取用户详情
     *
     * @param userId 用户ID
     * @return 用户详情
     */
    @ApiOperation("获取用户详情")
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/{userId}")
    public Result<SysUser> getInfo(@ApiParam("用户ID") @PathVariable Long userId) {
        return Result.success(sysUserService.getById(userId));
    }

    /**
     * 新增用户
     *
     * @param user 用户信息
     * @return 结果
     */
    @ApiOperation("新增用户")
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @Log(title = "用户管理", action = BusinessType.INSERT)
    @PostMapping
    public Result<Void> add(@Validated @RequestBody SysUser user) {
        if (!sysUserService.checkUsernameUnique(user)) {
            return Result.error("新增用户'" + user.getUsername() + "'失败，用户名已存在");
        }
        return sysUserService.save(user) ? Result.success() : Result.error("新增用户失败");
    }

    /**
     * 修改用户
     *
     * @param user 用户信息
     * @return 结果
     */
    @ApiOperation("修改用户")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", action = BusinessType.UPDATE)
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody SysUser user) {
        return sysUserService.updateById(user) ? Result.success() : Result.error("修改用户失败");
    }

    /**
     * 删除用户
     *
     * @param userIds 用户ID列表
     * @return 结果
     */
    @ApiOperation("删除用户")
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Log(title = "用户管理", action = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public Result<Void> remove(@ApiParam("用户ID列表") @PathVariable Long[] userIds) {
        return sysUserService.removeByIds(Arrays.asList(userIds)) ? Result.success() : Result.error("删除用户失败");
    }

    /**
     * 重置密码
     *
     * @param resetPwdDto 重置密码请求
     * @return 结果
     */
    @ApiOperation("重置密码")
    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
    @Log(title = "用户管理", action = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public Result<Void> resetPwd(@Validated @RequestBody ResetPwdDto resetPwdDto) {
        SysUser user = new SysUser();
        user.setId(resetPwdDto.getUserId());
        user.setPassword(resetPwdDto.getPassword());
        return sysUserService.resetPwd(user) > 0 
                ? Result.success() : Result.error("重置密码失败");
    }

    /**
     * 修改用户状态
     *
     * @param statusDto 状态请求
     * @return 结果
     */
    @ApiOperation("修改用户状态")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", action = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public Result<Void> changeStatus(@Validated @RequestBody UserStatusDto statusDto) {
        SysUser user = new SysUser();
        user.setId(statusDto.getUserId());
        user.setStatus(statusDto.getStatus());
        return sysUserService.updateStatus(user) > 0 
                ? Result.success() : Result.error("修改用户状态失败");
    }
}
