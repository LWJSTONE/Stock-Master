package com.graduation.inventory.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.inventory.business.entity.BusPurchaseOrder;
import com.graduation.inventory.common.domain.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 采购服务接口
 *
 * @author graduation
 * @version 1.0.0
 */
public interface PurchaseService {

    /**
     * 分页查询采购订单列表
     *
     * @param page       分页对象
     * @param purchaseNo 采购单号
     * @param supplierId 供应商ID
     * @param status     状态
     * @return 分页结果
     */
    PageResult<Map<String, Object>> list(Page<Map<String, Object>> page, String purchaseNo, Long supplierId, Integer status);

    /**
     * 查询采购订单详情（含明细）
     *
     * @param id 采购订单ID
     * @return 采购订单详情
     */
    Map<String, Object> getInfo(Long id);

    /**
     * 新增采购订单（状态0待审核）
     *
     * @param order 采购订单
     * @param items 采购明细列表
     * @return 是否成功
     */
    boolean add(BusPurchaseOrder order, List<Map<String, Object>> items);

    /**
     * 审核采购订单（状态1已审核）
     *
     * @param id     采购订单ID
     * @param auditBy 审核人ID
     * @return 是否成功
     */
    boolean audit(Long id, Long auditBy);

    /**
     * 执行入库（状态2已入库，调用StockService.inStock）
     *
     * @param id          采购订单ID
     * @param warehouseId 入库仓库ID
     * @return 是否成功
     */
    boolean doInStock(Long id, Long warehouseId);

    /**
     * 取消订单（状态3已取消）
     *
     * @param id 采购订单ID
     * @return 是否成功
     */
    boolean cancel(Long id);

    /**
     * 删除订单
     *
     * @param id 采购订单ID
     * @return 是否成功
     */
    boolean delete(Long id);
}
