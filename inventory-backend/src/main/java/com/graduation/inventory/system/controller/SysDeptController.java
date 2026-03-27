package com.graduation.inventory.system.controller;

import com.graduation.inventory.common.annotation.Log;
import com.graduation.inventory.common.domain.Result;
import com.graduation.inventory.common.enums.BusinessType;
import com.graduation.inventory.system.entity.SysDept;
import com.graduation.inventory.system.entity.dto.DeptTreeVo;
import com.graduation.inventory.system.service.SysDeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门控制器
 *
 * @author graduation
 * @version 1.0.0
 */
@Api(tags = "部门管理")
@RestController
@RequestMapping("/system/dept")
@RequiredArgsConstructor
public class SysDeptController {

    private final SysDeptService sysDeptService;

    /**
     * 查询部门列表
     *
     * @param deptName 部门名称
     * @param status   状态
     * @return 部门列表
     */
    @ApiOperation("查询部门列表")
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list")
    public Result<List<SysDept>> list(
            @ApiParam("部门名称") @RequestParam(required = false) String deptName,
            @ApiParam("状态") @RequestParam(required = false) String status) {
        SysDept dept = new SysDept();
        dept.setDeptName(deptName);
        dept.setStatus(status);
        List<SysDept> depts = sysDeptService.selectDeptList(dept);
        return Result.success(depts);
    }

    /**
     * 获取部门详情
     *
     * @param deptId 部门ID
     * @return 部门详情
     */
    @ApiOperation("获取部门详情")
    @PreAuthorize("@ss.hasPermi('system:dept:query')")
    @GetMapping("/{deptId}")
    public Result<SysDept> getInfo(@ApiParam("部门ID") @PathVariable Long deptId) {
        return Result.success(sysDeptService.getById(deptId));
    }

    /**
     * 获取部门树
     *
     * @return 部门树
     */
    @ApiOperation("获取部门树")
    @GetMapping("/tree")
    public Result<List<SysDept>> tree() {
        List<SysDept> depts = sysDeptService.selectDeptList(new SysDept());
        List<SysDept> tree = sysDeptService.buildDeptTree(depts);
        return Result.success(tree);
    }

    /**
     * 新增部门
     *
     * @param dept 部门信息
     * @return 结果
     */
    @ApiOperation("新增部门")
    @PreAuthorize("@ss.hasPermi('system:dept:add')")
    @Log(title = "部门管理", action = BusinessType.INSERT)
    @PostMapping
    public Result<Void> add(@Validated @RequestBody SysDept dept) {
        if (!sysDeptService.checkDeptNameUnique(dept)) {
            return Result.error("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        return sysDeptService.save(dept) ? Result.success() : Result.error("新增部门失败");
    }

    /**
     * 修改部门
     *
     * @param dept 部门信息
     * @return 结果
     */
    @ApiOperation("修改部门")
    @PreAuthorize("@ss.hasPermi('system:dept:edit')")
    @Log(title = "部门管理", action = BusinessType.UPDATE)
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody SysDept dept) {
        if (!sysDeptService.checkDeptNameUnique(dept)) {
            return Result.error("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        if (dept.getId().equals(dept.getParentId())) {
            return Result.error("修改部门'" + dept.getDeptName() + "'失败，上级部门不能选择自己");
        }
        return sysDeptService.updateById(dept) ? Result.success() : Result.error("修改部门失败");
    }

    /**
     * 删除部门
     *
     * @param deptId 部门ID
     * @return 结果
     */
    @ApiOperation("删除部门")
    @PreAuthorize("@ss.hasPermi('system:dept:remove')")
    @Log(title = "部门管理", action = BusinessType.DELETE)
    @DeleteMapping("/{deptId}")
    public Result<Void> remove(@ApiParam("部门ID") @PathVariable Long deptId) {
        if (sysDeptService.hasChildByDeptId(deptId)) {
            return Result.error("存在子部门,不允许删除");
        }
        if (sysDeptService.checkDeptExistUser(deptId)) {
            return Result.error("部门存在用户,不允许删除");
        }
        return sysDeptService.removeById(deptId) ? Result.success() : Result.error("删除部门失败");
    }
}
