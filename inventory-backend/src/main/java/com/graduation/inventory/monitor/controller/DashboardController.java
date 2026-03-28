package com.graduation.inventory.monitor.controller;

import com.graduation.inventory.common.annotation.Log;
import com.graduation.inventory.common.domain.Result;
import com.graduation.inventory.common.enums.BusinessType;
import com.graduation.inventory.monitor.entity.vo.CategoryRatioVo;
import com.graduation.inventory.monitor.entity.vo.OverviewVo;
import com.graduation.inventory.monitor.entity.vo.StockWarningVo;
import com.graduation.inventory.monitor.entity.vo.TopProductVo;
import com.graduation.inventory.monitor.entity.vo.TrendVo;
import com.graduation.inventory.monitor.service.DashboardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 数据大屏控制器
 *
 * @author graduation
 * @version 1.0.0
 */
@Api(tags = "数据大屏")
@RestController
@RequestMapping("/monitor/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 获取概览数据
     *
     * @return 概览数据
     */
    @ApiOperation("获取概览数据")
    @GetMapping("/overview")
    public Result<OverviewVo> getOverview() {
        return Result.success(dashboardService.getOverview());
    }

    /**
     * 获取出入库趋势
     *
     * @return 趋势数据列表
     */
    @ApiOperation("获取出入库趋势")
    @GetMapping("/trend")
    public Result<List<TrendVo>> getTrend() {
        return Result.success(dashboardService.getInboundOutboundTrend());
    }

    /**
     * 获取分类库存占比
     *
     * @return 分类占比列表
     */
    @ApiOperation("获取分类库存占比")
    @GetMapping("/category")
    public Result<List<CategoryRatioVo>> getCategory() {
        return Result.success(dashboardService.getCategoryStockRatio());
    }

    /**
     * 获取库存预警
     *
     * @return 预警列表
     */
    @ApiOperation("获取库存预警")
    @GetMapping("/warning")
    public Result<List<StockWarningVo>> getWarning() {
        return Result.success(dashboardService.getStockWarningList());
    }

    /**
     * 获取TOP商品
     *
     * @return TOP商品列表
     */
    @ApiOperation("获取TOP商品")
    @GetMapping("/topProducts")
    public Result<List<TopProductVo>> getTopProducts() {
        return Result.success(dashboardService.getTopProducts());
    }
}
