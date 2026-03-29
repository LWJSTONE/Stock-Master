package com.graduation.inventory.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.inventory.business.entity.BusSaleOrder;
import com.graduation.inventory.business.entity.dto.AuditDto;
import com.graduation.inventory.business.entity.dto.SaleOrderDto;
import com.graduation.inventory.business.service.BusSaleOrderService;
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
 * 销售控制器
 *
 * @author graduation
 * @version 1.0.0
 */
@Api(tags = "销售管理")
@RestController
@RequestMapping("/business/sale")
@RequiredArgsConstructor
public class SaleController {

    private final BusSaleOrderService saleOrderService;

    /**
     * 分页查询销售订单列表
     */
    @ApiOperation("分页查询销售订单列表")
    @PreAuthorize("@ss.hasPermi('business:sale:list')")
    @GetMapping("/list")
    public Result<PageResult<BusSaleOrder>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("销售单号") @RequestParam(required = false) String saleNo,
            @ApiParam("客户ID") @RequestParam(required = false) Long customerId,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        Page<BusSaleOrder> page = new Page<>(pageNum, pageSize);
        Page<BusSaleOrder> result = saleOrderService.selectSalePage(page, saleNo, customerId, status);
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal()));
    }

    /**
     * 获取销售订单详情
     */
    @ApiOperation("获取销售订单详情")
    @PreAuthorize("@ss.hasPermi('business:sale:query')")
    @GetMapping("/{saleId}")
    public Result<BusSaleOrder> getInfo(@ApiParam("销售订单ID") @PathVariable Long saleId) {
        return Result.success(saleOrderService.selectSaleById(saleId));
    }

    /**
     * 新增销售订单
     */
    @ApiOperation("新增销售订单")
    @PreAuthorize("@ss.hasPermi('business:sale:add')")
    @Log(title = "销售管理", action = BusinessType.INSERT)
    @PostMapping
    public Result<Void> add(@Validated @RequestBody SaleOrderDto dto) {
        return saleOrderService.insertSale(dto) ? Result.success() : Result.error("新增销售订单失败");
    }

    /**
     * 更新销售订单
     */
    @ApiOperation("更新销售订单")
    @PreAuthorize("@ss.hasPermi('business:sale:edit')")
    @Log(title = "销售管理", action = BusinessType.UPDATE)
    @PutMapping
    public Result<Void> update(@Validated @RequestBody SaleOrderDto dto) {
        return saleOrderService.updateSale(dto) ? Result.success() : Result.error("更新销售订单失败");
    }

    /**
     * 审核销售订单
     */
    @ApiOperation("审核销售订单")
    @PreAuthorize("@ss.hasPermi('business:sale:audit')")
    @Log(title = "销售管理", action = BusinessType.UPDATE)
    @PutMapping("/audit")
    public Result<Void> audit(@Validated @RequestBody AuditDto dto) {
        return saleOrderService.auditSale(dto) ? Result.success() : Result.error("审核销售订单失败");
    }

    /**
     * 执行出库
     */
    @ApiOperation("执行出库")
    @PreAuthorize("@ss.hasPermi('business:sale:outstock')")
    @Log(title = "销售管理", action = BusinessType.UPDATE)
    @PutMapping("/outstock")
    public Result<Void> outstock(@ApiParam("销售订单ID") @RequestParam Long saleId) {
        return saleOrderService.outstock(saleId) ? Result.success() : Result.error("出库失败");
    }

    /**
     * 取消订单
     */
    @ApiOperation("取消订单")
    @PreAuthorize("@ss.hasPermi('business:sale:cancel')")
    @Log(title = "销售管理", action = BusinessType.UPDATE)
    @PutMapping("/cancel")
    public Result<Void> cancel(@ApiParam("销售订单ID") @RequestParam Long saleId) {
        return saleOrderService.cancelSale(saleId) ? Result.success() : Result.error("取消订单失败");
    }

    /**
     * 删除订单
     */
    @ApiOperation("删除订单")
    @PreAuthorize("@ss.hasPermi('business:sale:remove')")
    @Log(title = "销售管理", action = BusinessType.DELETE)
    @DeleteMapping("/{saleIds}")
    public Result<Void> remove(@ApiParam("销售订单ID数组") @PathVariable Long[] saleIds) {
        return saleOrderService.deleteSaleByIds(saleIds) ? Result.success() : Result.error("删除订单失败");
    }
}
