package com.graduation.inventory.base.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.inventory.base.entity.BaseSupplier;
import com.graduation.inventory.base.service.BaseSupplierService;
import com.graduation.inventory.common.annotation.Log;
import com.graduation.inventory.common.domain.PageResult;
import com.graduation.inventory.common.domain.Result;
import com.graduation.inventory.common.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 供应商控制器
 *
 * @author graduation
 * @version 1.0.0
 */
@Api(tags = "供应商管理")
@RestController
@RequestMapping("/base/supplier")
@RequiredArgsConstructor
public class BaseSupplierController {

    private final BaseSupplierService supplierService;

    /**
     * 分页查询供应商列表
     */
    @ApiOperation("分页查询供应商列表")
    @PreAuthorize("@ss.hasPermi('base:supplier:list')")
    @GetMapping("/list")
    public Result<PageResult<BaseSupplier>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("供应商编码") @RequestParam(required = false) String supCode,
            @ApiParam("供应商名称") @RequestParam(required = false) String supName) {
        Page<BaseSupplier> page = new Page<>(pageNum, pageSize);
        Page<BaseSupplier> result = supplierService.selectSupplierPage(page, supCode, supName);
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal()));
    }

    /**
     * 获取供应商详情
     */
    @ApiOperation("获取供应商详情")
    @PreAuthorize("@ss.hasPermi('base:supplier:query')")
    @GetMapping("/{id}")
    public Result<BaseSupplier> getInfo(@ApiParam("供应商ID") @PathVariable Long id) {
        return Result.success(supplierService.selectSupplierById(id));
    }

    /**
     * 查询所有供应商
     */
    @ApiOperation("查询所有供应商")
    @GetMapping("/listAll")
    public Result<List<BaseSupplier>> listAll() {
        return Result.success(supplierService.selectAllSupplier());
    }

    /**
     * 新增供应商
     */
    @ApiOperation("新增供应商")
    @PreAuthorize("@ss.hasPermi('base:supplier:add')")
    @Log(title = "供应商管理", action = BusinessType.INSERT)
    @PostMapping
    public Result<Void> add(@Validated @RequestBody BaseSupplier supplier) {
        return supplierService.insertSupplier(supplier) ? Result.success() : Result.error("新增供应商失败");
    }

    /**
     * 修改供应商
     */
    @ApiOperation("修改供应商")
    @PreAuthorize("@ss.hasPermi('base:supplier:edit')")
    @Log(title = "供应商管理", action = BusinessType.UPDATE)
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody BaseSupplier supplier) {
        return supplierService.updateSupplier(supplier) ? Result.success() : Result.error("修改供应商失败");
    }

    /**
     * 删除供应商
     */
    @ApiOperation("删除供应商")
    @PreAuthorize("@ss.hasPermi('base:supplier:remove')")
    @Log(title = "供应商管理", action = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public Result<Void> remove(@ApiParam("供应商ID") @PathVariable Long id) {
        return supplierService.deleteSupplierById(id) ? Result.success() : Result.error("删除供应商失败");
    }

    /**
     * 批量删除供应商
     */
    @ApiOperation("批量删除供应商")
    @PreAuthorize("@ss.hasPermi('base:supplier:remove')")
    @Log(title = "供应商管理", action = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public Result<Void> removeBatch(@RequestBody List<Long> ids) {
        return supplierService.deleteSupplierByIds(ids) ? Result.success() : Result.error("批量删除供应商失败");
    }

    /**
     * 校验供应商编码是否唯一
     */
    @ApiOperation("校验供应商编码是否唯一")
    @GetMapping("/checkSupCodeUnique")
    public Result<Boolean> checkSupCodeUnique(
            @ApiParam("供应商编码") @RequestParam String supCode,
            @ApiParam("供应商ID") @RequestParam(required = false) Long id) {
        BaseSupplier supplier = new BaseSupplier();
        supplier.setSupCode(supCode);
        supplier.setId(id);
        return Result.success(supplierService.checkSupCodeUnique(supplier));
    }
}
