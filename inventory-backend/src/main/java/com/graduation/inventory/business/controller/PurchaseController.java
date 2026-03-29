package com.graduation.inventory.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.inventory.business.entity.BusPurchaseOrder;
import com.graduation.inventory.business.entity.dto.AuditDto;
import com.graduation.inventory.business.entity.dto.PurchaseOrderDto;
import com.graduation.inventory.business.service.BusPurchaseOrderService;
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

/**
 * 采购控制器
 *
 * @author graduation
 * @version 1.0.0
 */
@Api(tags = "采购管理")
@RestController
@RequestMapping("/business/purchase")
@RequiredArgsConstructor
public class PurchaseController {

    private final BusPurchaseOrderService purchaseOrderService;

    /**
     * 分页查询采购订单列表
     */
    @ApiOperation("分页查询采购订单列表")
    @PreAuthorize("@ss.hasPermi('business:purchase:list')")
    @GetMapping("/list")
    public Result<PageResult<BusPurchaseOrder>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("采购单号") @RequestParam(required = false) String purchaseNo,
            @ApiParam("供应商ID") @RequestParam(required = false) Long supplierId,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        Page<BusPurchaseOrder> page = new Page<>(pageNum, pageSize);
        Page<BusPurchaseOrder> result = purchaseOrderService.selectPurchasePage(page, purchaseNo, supplierId, status);
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal()));
    }

    /**
     * 获取采购订单详情
     */
    @ApiOperation("获取采购订单详情")
    @PreAuthorize("@ss.hasPermi('business:purchase:query')")
    @GetMapping("/{purchaseId}")
    public Result<BusPurchaseOrder> getInfo(@ApiParam("采购订单ID") @PathVariable Long purchaseId) {
        return Result.success(purchaseOrderService.selectPurchaseById(purchaseId));
    }

    /**
     * 新增采购订单
     */
    @ApiOperation("新增采购订单")
    @PreAuthorize("@ss.hasPermi('business:purchase:add')")
    @Log(title = "采购管理", action = BusinessType.INSERT)
    @PostMapping
    public Result<Void> add(@Validated @RequestBody PurchaseOrderDto dto) {
        return purchaseOrderService.insertPurchase(dto) ? Result.success() : Result.error("新增采购订单失败");
    }

    /**
     * 更新采购订单
     */
    @ApiOperation("更新采购订单")
    @PreAuthorize("@ss.hasPermi('business:purchase:edit')")
    @Log(title = "采购管理", action = BusinessType.UPDATE)
    @PutMapping
    public Result<Void> update(@Validated @RequestBody PurchaseOrderDto dto) {
        return purchaseOrderService.updatePurchase(dto) ? Result.success() : Result.error("更新采购订单失败");
    }

    /**
     * 审核采购订单
     */
    @ApiOperation("审核采购订单")
    @PreAuthorize("@ss.hasPermi('business:purchase:audit')")
    @Log(title = "采购管理", action = BusinessType.UPDATE)
    @PutMapping("/audit")
    public Result<Void> audit(@Validated @RequestBody AuditDto dto) {
        return purchaseOrderService.auditPurchase(dto) ? Result.success() : Result.error("审核采购订单失败");
    }

    /**
     * 执行入库
     */
    @ApiOperation("执行入库")
    @PreAuthorize("@ss.hasPermi('business:purchase:instock')")
    @Log(title = "采购管理", action = BusinessType.UPDATE)
    @PutMapping("/instock")
    public Result<Void> instock(
            @ApiParam("采购订单ID") @RequestParam Long purchaseId,
            @ApiParam("仓库ID") @RequestParam Long warehouseId) {
        return purchaseOrderService.instock(purchaseId, warehouseId) ? Result.success() : Result.error("入库失败");
    }

    /**
     * 取消订单
     */
    @ApiOperation("取消订单")
    @PreAuthorize("@ss.hasPermi('business:purchase:cancel')")
    @Log(title = "采购管理", action = BusinessType.UPDATE)
    @PutMapping("/cancel")
    public Result<Void> cancel(@ApiParam("采购订单ID") @RequestParam Long purchaseId) {
        return purchaseOrderService.cancelPurchase(purchaseId) ? Result.success() : Result.error("取消订单失败");
    }

    /**
     * 删除订单
     */
    @ApiOperation("删除订单")
    @PreAuthorize("@ss.hasPermi('business:purchase:remove')")
    @Log(title = "采购管理", action = BusinessType.DELETE)
    @DeleteMapping("/{purchaseIds}")
    public Result<Void> remove(@ApiParam("采购订单ID数组") @PathVariable Long[] purchaseIds) {
        return purchaseOrderService.deletePurchaseByIds(purchaseIds) ? Result.success() : Result.error("删除订单失败");
    }
}
