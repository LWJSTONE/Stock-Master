package com.graduation.inventory.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.inventory.business.entity.BusSaleOrder;
import com.graduation.inventory.common.domain.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 销售服务接口
 *
 * @author graduation
 * @version 1.0.0
 */
public interface SaleService {

    /**
     * 分页查询销售订单列表
     *
     * @param page    分页对象
     * @param saleNo  销售单号
     * @param customerId 客户ID
     * @param status  状态
     * @return 分页结果
     */
    PageResult<Map<String, Object>> list(Page<Map<String, Object>> page, String saleNo, Long customerId, Integer status);

    /**
     * 查询销售订单详情
     *
     * @param id 销售订单ID
     * @return 销售订单详情
     */
    Map<String, Object> getInfo(Long id);

    /**
     * 新增销售订单（冻结库存）
     *
     * @param order 销售订单
     * @param items 销售明细列表
     * @param warehouseId 出库仓库ID
     * @return 是否成功
     */
    boolean add(BusSaleOrder order, List<Map<String, Object>> items, Long warehouseId);

    /**
     * 审核销售订单
     *
     * @param id 销售订单ID
     * @return 是否成功
     */
    boolean audit(Long id);

    /**
     * 执行出库（解冻并扣减库存）
     *
     * @param id 销售订单ID
     * @return 是否成功
     */
    boolean doOutStock(Long id);

    /**
     * 取消订单（解冻库存）
     *
     * @param id 销售订单ID
     * @return 是否成功
     */
    boolean cancel(Long id);

    /**
     * 删除订单
     *
     * @param id 销售订单ID
     * @return 是否成功
     */
    boolean delete(Long id);
}
