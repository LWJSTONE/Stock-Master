package com.graduation.inventory.monitor.service;

import com.graduation.inventory.monitor.entity.vo.CategoryRatioVo;
import com.graduation.inventory.monitor.entity.vo.OverviewVo;
import com.graduation.inventory.monitor.entity.vo.StockWarningVo;
import com.graduation.inventory.monitor.entity.vo.TopProductVo;
import com.graduation.inventory.monitor.entity.vo.TrendVo;

import java.util.List;

/**
 * 大屏数据服务接口
 *
 * @author graduation
 * @version 1.0.0
 */
public interface DashboardService {

    /**
     * 获取概览数据
     *
     * @return 概览数据
     */
    OverviewVo getOverview();

    /**
     * 获取近7天出入库趋势数据
     *
     * @return 趋势数据列表
     */
    List<TrendVo> getInboundOutboundTrend();

    /**
     * 获取商品分类库存占比
     *
     * @return 分类占比列表
     */
    List<CategoryRatioVo> getCategoryStockRatio();

    /**
     * 获取库存预警列表
     *
     * @return 预警列表
     */
    List<StockWarningVo> getStockWarningList();

    /**
     * 获取销量TOP10商品
     *
     * @return TOP商品列表
     */
    List<TopProductVo> getTopProducts();
}
