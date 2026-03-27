package com.graduation.inventory.base.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.inventory.base.entity.BaseWarehouse;
import com.graduation.inventory.base.service.BaseWarehouseService;
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
 * 仓库控制器
 *
 * @author graduation
 * @version 1.0.0
 */
@Api(tags = "仓库管理")
@RestController
@RequestMapping("/base/warehouse")
@RequiredArgsConstructor
public class BaseWarehouseController {

    private final BaseWarehouseService warehouseService;

    /**
     * 分页查询仓库列表
     */
    @ApiOperation("分页查询仓库列表")
    @PreAuthorize("@ss.hasPermi('base:warehouse:list')")
    @GetMapping("/list")
    public Result<PageResult<BaseWarehouse>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("仓库编码") @RequestParam(required = false) String whCode,
            @ApiParam("仓库名称") @RequestParam(required = false) String whName,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        Page<BaseWarehouse> page = new Page<>(pageNum, pageSize);
        Page<BaseWarehouse> result = warehouseService.selectWarehousePage(page, whCode, whName, status);
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal()));
    }

    /**
     * 获取仓库详情
     */
    @ApiOperation("获取仓库详情")
    @PreAuthorize("@ss.hasPermi('base:warehouse:query')")
    @GetMapping("/{id}")
    public Result<BaseWarehouse> getInfo(@ApiParam("仓库ID") @PathVariable Long id) {
        return Result.success(warehouseService.selectWarehouseById(id));
    }

    /**
     * 查询所有启用的仓库
     */
    @ApiOperation("查询所有启用的仓库")
    @GetMapping("/listAll")
    public Result<List<BaseWarehouse>> listAll() {
        return Result.success(warehouseService.selectAllEnabledWarehouse());
    }

    /**
     * 新增仓库
     */
    @ApiOperation("新增仓库")
    @PreAuthorize("@ss.hasPermi('base:warehouse:add')")
    @Log(title = "仓库管理", action = BusinessType.INSERT)
    @PostMapping
    public Result<Void> add(@Validated @RequestBody BaseWarehouse warehouse) {
        return warehouseService.insertWarehouse(warehouse) ? Result.success() : Result.error("新增仓库失败");
    }

    /**
     * 修改仓库
     */
    @ApiOperation("修改仓库")
    @PreAuthorize("@ss.hasPermi('base:warehouse:edit')")
    @Log(title = "仓库管理", action = BusinessType.UPDATE)
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody BaseWarehouse warehouse) {
        return warehouseService.updateWarehouse(warehouse) ? Result.success() : Result.error("修改仓库失败");
    }

    /**
     * 删除仓库
     */
    @ApiOperation("删除仓库")
    @PreAuthorize("@ss.hasPermi('base:warehouse:remove')")
    @Log(title = "仓库管理", action = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public Result<Void> remove(@ApiParam("仓库ID") @PathVariable Long id) {
        return warehouseService.deleteWarehouseById(id) ? Result.success() : Result.error("删除仓库失败");
    }

    /**
     * 批量删除仓库
     */
    @ApiOperation("批量删除仓库")
    @PreAuthorize("@ss.hasPermi('base:warehouse:remove')")
    @Log(title = "仓库管理", action = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public Result<Void> removeBatch(@RequestBody List<Long> ids) {
        return warehouseService.deleteWarehouseByIds(ids) ? Result.success() : Result.error("批量删除仓库失败");
    }

    /**
     * 校验仓库编码是否唯一
     */
    @ApiOperation("校验仓库编码是否唯一")
    @GetMapping("/checkWhCodeUnique")
    public Result<Boolean> checkWhCodeUnique(
            @ApiParam("仓库编码") @RequestParam String whCode,
            @ApiParam("仓库ID") @RequestParam(required = false) Long id) {
        BaseWarehouse warehouse = new BaseWarehouse();
        warehouse.setWhCode(whCode);
        warehouse.setId(id);
        return Result.success(warehouseService.checkWhCodeUnique(warehouse));
    }
}
