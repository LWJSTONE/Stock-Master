package com.graduation.inventory.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.graduation.inventory.business.entity.StockMain;
import com.graduation.inventory.business.entity.StockRecord;

import java.util.List;
import java.util.Map;

/**
 * 库存服务接口
 *
 * @author graduation
 * @version 1.0.0
 */
public interface StockMainService extends IService<StockMain> {

    /**
     * 分页查询实时库存
     *
     * @param page        分页对象
     * @param warehouseId 仓库ID
     * @param skuCode     SKU编码
     * @param skuName     SKU名称
     * @return 分页结果
     */
    Page<StockMain> selectStockPage(Page<StockMain> page, Long warehouseId, String skuCode, String skuName);

    /**
     * 根据ID查询库存详情
     *
     * @param id 库存ID
     * @return 库存详情
     */
    StockMain selectStockById(Long id);

    /**
     * 分页查询库存流水
     *
     * @param page        分页对象
     * @param warehouseId 仓库ID
     * @param orderNo     订单号
     * @param orderType   订单类型
     * @return 分页结果
     */
    Page<StockRecord> selectRecordPage(Page<StockRecord> page, Long warehouseId, String orderNo, Integer orderType);

    /**
     * 查询库存预警列表
     *
     * @param warehouseId 仓库ID
     * @return 预警列表
     */
    List<Map<String, Object>> selectWarningList(Long warehouseId);
}
