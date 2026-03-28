package com.graduation.inventory.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.inventory.business.entity.BusStockCheck;
import com.graduation.inventory.business.entity.dto.StockCheckDto;
import com.graduation.inventory.business.service.BusStockCheckService;
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
 * 盘点控制器
 *
 * @author graduation
 * @version 1.0.0
 */
@Api(tags = "盘点管理")
@RestController
@RequestMapping("/stock/check")
@RequiredArgsConstructor
public class StockCheckController {

    private final BusStockCheckService stockCheckService;

    /**
     * 分页查询盘点列表
     */
    @ApiOperation("分页查询盘点列表")
    @PreAuthorize("@ss.hasPermi('stock:check:list')")
    @GetMapping("/list")
    public Result<PageResult<BusStockCheck>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("盘点单号") @RequestParam(required = false) String checkNo,
            @ApiParam("仓库ID") @RequestParam(required = false) Long warehouseId,
            @ApiParam("盘点状态") @RequestParam(required = false) Integer checkStatus) {
        Page<BusStockCheck> page = new Page<>(pageNum, pageSize);
        Page<BusStockCheck> result = stockCheckService.selectCheckPage(page, checkNo, warehouseId, checkStatus);
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal()));
    }

    /**
     * 获取盘点详情
     */
    @ApiOperation("获取盘点详情")
    @PreAuthorize("@ss.hasPermi('stock:check:query')")
    @GetMapping("/{checkId}")
    public Result<BusStockCheck> getInfo(@ApiParam("盘点ID") @PathVariable Long checkId) {
        return Result.success(stockCheckService.selectCheckById(checkId));
    }

    /**
     * 新增盘点单
     */
    @ApiOperation("新增盘点单")
    @PreAuthorize("@ss.hasPermi('stock:check:add')")
    @Log(title = "盘点管理", action = BusinessType.INSERT)
    @PostMapping
    public Result<Void> add(@Validated @RequestBody StockCheckDto dto) {
        return stockCheckService.insertCheck(dto) ? Result.success() : Result.error("新增盘点单失败");
    }

    /**
     * 提交实盘数量
     */
    @ApiOperation("提交实盘数量")
    @PreAuthorize("@ss.hasPermi('stock:check:edit')")
    @Log(title = "盘点管理", action = BusinessType.UPDATE)
    @PutMapping("/item")
    public Result<Void> submitItem(@RequestBody StockCheckDto dto) {
        return stockCheckService.submitActualQty(dto) ? Result.success() : Result.error("提交实盘数量失败");
    }

    /**
     * 确认盘点
     */
    @ApiOperation("确认盘点")
    @PreAuthorize("@ss.hasPermi('stock:check:confirm')")
    @Log(title = "盘点管理", action = BusinessType.UPDATE)
    @PutMapping("/confirm")
    public Result<Void> confirm(@ApiParam("盘点ID") @RequestParam Long checkId) {
        return stockCheckService.confirmCheck(checkId) ? Result.success() : Result.error("确认盘点失败");
    }

    /**
     * 删除盘点单
     */
    @ApiOperation("删除盘点单")
    @PreAuthorize("@ss.hasPermi('stock:check:remove')")
    @Log(title = "盘点管理", action = BusinessType.DELETE)
    @DeleteMapping("/{checkIds}")
    public Result<Void> remove(@ApiParam("盘点ID数组") @PathVariable Long[] checkIds) {
        return stockCheckService.deleteCheckByIds(checkIds) ? Result.success() : Result.error("删除盘点单失败");
    }
}
