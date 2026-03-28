package com.graduation.inventory.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.inventory.business.entity.StockMain;
import com.graduation.inventory.business.entity.StockRecord;
import com.graduation.inventory.business.service.StockMainService;
import com.graduation.inventory.common.domain.PageResult;
import com.graduation.inventory.common.domain.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 库存控制器
 *
 * @author graduation
 * @version 1.0.0
 */
@Api(tags = "库存管理")
@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockMainService stockMainService;

    /**
     * 分页查询实时库存
     */
    @ApiOperation("分页查询实时库存")
    @GetMapping("/main/list")
    public Result<Map<String, Object>> listStock(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("仓库ID") @RequestParam(required = false) Long warehouseId,
            @ApiParam("关键词") @RequestParam(required = false) String keyword) {
        Map<String, Object> result = stockMainService.selectStockListWithInfo(pageNum, pageSize, warehouseId, keyword);
        return Result.success(result);
    }

    /**
     * 获取库存详情
     */
    @ApiOperation("获取库存详情")
    @PreAuthorize("@ss.hasPermi('stock:main:query')")
    @GetMapping("/main/{id}")
    public Result<StockMain> getStockInfo(@ApiParam("库存ID") @PathVariable Long id) {
        return Result.success(stockMainService.selectStockById(id));
    }

    /**
     * 分页查询库存流水
     */
    @ApiOperation("分页查询库存流水")
    @PreAuthorize("@ss.hasPermi('stock:record:list')")
    @GetMapping("/record/list")
    public Result<PageResult<StockRecord>> listRecord(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("仓库ID") @RequestParam(required = false) Long warehouseId,
            @ApiParam("订单号") @RequestParam(required = false) String orderNo,
            @ApiParam("订单类型") @RequestParam(required = false) Integer orderType) {
        Page<StockRecord> page = new Page<>(pageNum, pageSize);
        Page<StockRecord> result = stockMainService.selectRecordPage(page, warehouseId, orderNo, orderType);
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal()));
    }

    /**
     * 库存预警列表
     */
    @ApiOperation("库存预警列表")
    @PreAuthorize("@ss.hasPermi('stock:warning:list')")
    @GetMapping("/warning")
    public Result<List<Map<String, Object>>> warningList(
            @ApiParam("仓库ID") @RequestParam(required = false) Long warehouseId) {
        return Result.success(stockMainService.selectWarningList(warehouseId));
    }
}
