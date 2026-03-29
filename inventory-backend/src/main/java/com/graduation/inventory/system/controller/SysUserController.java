package com.graduation.inventory.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
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
        return sysUserService.resetPwd(user) > 0 ? Result.success() : Result.error("重置密码失败");
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
        return sysUserService.updateStatus(user) > 0 ? Result.success() : Result.error("修改用户状态失败");
    }

    /**
     * 导出用户数据
     *
     * @param response HTTP响应
     */
    @ApiOperation("导出用户数据")
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @Log(title = "用户管理", action = BusinessType.EXPORT)
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        // 查询所有用户
        List<SysUser> list = sysUserService.selectUserList(new SysUser());

        // 设置响应头
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=users.csv");

        // 写入CSV
        try (PrintWriter writer = response.getWriter()) {
            // 写入BOM以支持中文
            writer.write('\ufeff');
            // 写入表头
            writer.println("ID,用户名,姓名,手机号,邮箱,状态,创建时间");
            // 写入数据
            for (SysUser user : list) {
                writer.println(String.format("%d,%s,%s,%s,%s,%s,%s",
                        user.getId(),
                        user.getUsername(),
                        user.getRealName() != null ? user.getRealName() : "",
                        user.getPhone() != null ? user.getPhone() : "",
                        user.getEmail() != null ? user.getEmail() : "",
                        "0".equals(user.getStatus()) ? "启用" : "禁用",
                        user.getCreateTime() != null ? user.getCreateTime().toString() : ""
                ));
            }
        }
    }
}
