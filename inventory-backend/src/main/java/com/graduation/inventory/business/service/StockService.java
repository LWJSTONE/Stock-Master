package com.graduation.inventory.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.inventory.business.entity.StockMain;
import com.graduation.inventory.business.entity.StockRecord;
import com.graduation.inventory.common.domain.PageResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 库存服务接口
 *
 * @author graduation
 * @version 1.0.0
 */
public interface StockService {

    /**
     * 分页查询实时库存
     *
     * @param page       分页对象
     * @param warehouseId 仓库ID
     * @param skuCode    SKU编码
     * @param skuName    SKU名称
     * @return 分页结果
     */
    PageResult<Map<String, Object>> getStockList(Page<Map<String, Object>> page, Long warehouseId, String skuCode, String skuName);

    /**
     * 查询库存详情（含仓库、SKU信息）
     *
     * @param warehouseId 仓库ID
     * @param skuId       SKU ID
     * @return 库存详情
     */
    Map<String, Object> getStockDetail(Long warehouseId, Long skuId);

    /**
     * 入库操作
     *
     * @param warehouseId 仓库ID
     * @param skuId       SKU ID
     * @param qty         入库数量
     * @param batchNo     批次号
     * @param orderNo     订单号
     * @param orderType   订单类型（1采购入库 2销售出库 3调拨入 4调拨出 5盘盈 6盘亏 7报损）
     */
    void inStock(Long warehouseId, Long skuId, BigDecimal qty, String batchNo, String orderNo, Integer orderType);

    /**
     * 出库操作
     *
     * @param warehouseId 仓库ID
     * @param skuId       SKU ID
     * @param qty         出库数量
     * @param orderNo     订单号
     * @param orderType   订单类型
     */
    void outStock(Long warehouseId, Long skuId, BigDecimal qty, String orderNo, Integer orderType);

    /**
     * 冻结库存
     *
     * @param warehouseId 仓库ID
     * @param skuId       SKU ID
     * @param qty         冻结数量
     */
    void freezeStock(Long warehouseId, Long skuId, BigDecimal qty);

    /**
     * 解冻库存
     *
     * @param warehouseId 仓库ID
     * @param skuId       SKU ID
     * @param qty         解冻数量
     */
    void unfreezeStock(Long warehouseId, Long skuId, BigDecimal qty);

    /**
     * 分页查询库存流水
     *
     * @param page        分页对象
     * @param warehouseId 仓库ID
     * @param skuId       SKU ID
     * @param orderType   订单类型
     * @param orderNo     订单号
     * @return 分页结果
     */
    PageResult<Map<String, Object>> getStockRecords(Page<Map<String, Object>> page, Long warehouseId, Long skuId, Integer orderType, String orderNo);

    /**
     * 检查库存预警
     *
     * @return 预警列表
     */
    List<Map<String, Object>> checkStockWarning();

    /**
     * 根据仓库ID和SKU ID查询库存
     *
     * @param warehouseId 仓库ID
     * @param skuId       SKU ID
     * @return 库存对象
     */
    StockMain getStockByWarehouseAndSku(Long warehouseId, Long skuId);

    /**
     * 检查库存是否充足
     *
     * @param warehouseId 仓库ID
     * @param skuId       SKU ID
     * @param qty         需要数量
     * @return 是否充足
     */
    boolean checkStockAvailable(Long warehouseId, Long skuId, BigDecimal qty);
}
