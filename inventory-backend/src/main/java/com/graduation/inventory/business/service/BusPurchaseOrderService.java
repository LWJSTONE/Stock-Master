package com.graduation.inventory.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.graduation.inventory.business.entity.BusPurchaseOrder;
import com.graduation.inventory.business.entity.dto.AuditDto;
import com.graduation.inventory.business.entity.dto.PurchaseOrderDto;

/**
 * 采购订单服务接口
 *
 * @author graduation
 * @version 1.0.0
 */
public interface BusPurchaseOrderService extends IService<BusPurchaseOrder> {

    /**
     * 分页查询采购订单列表
     *
     * @param page        分页对象
     * @param purchaseNo  采购单号
     * @param supplierId  供应商ID
     * @param status      状态
     * @return 分页结果
     */
    Page<BusPurchaseOrder> selectPurchasePage(Page<BusPurchaseOrder> page, String purchaseNo, Long supplierId, Integer status);

    /**
     * 根据ID查询采购订单详情
     *
     * @param purchaseId 采购订单ID
     * @return 采购订单详情
     */
    BusPurchaseOrder selectPurchaseById(Long purchaseId);

    /**
     * 新增采购订单
     *
     * @param dto 采购订单DTO
     * @return 是否成功
     */
    boolean insertPurchase(PurchaseOrderDto dto);

    /**
     * 审核采购订单
     *
     * @param dto 审核DTO
     * @return 是否成功
     */
    boolean auditPurchase(AuditDto dto);

    /**
     * 执行入库
     *
     * @param purchaseId 采购订单ID
     * @param warehouseId 入库仓库ID
     * @return 是否成功
     */
    boolean instock(Long purchaseId, Long warehouseId);

    /**
     * 取消订单
     *
     * @param purchaseId 采购订单ID
     * @return 是否成功
     */
    boolean cancelPurchase(Long purchaseId);

    /**
     * 批量删除采购订单
     *
     * @param purchaseIds 采购订单ID数组
     * @return 是否成功
     */
    boolean deletePurchaseByIds(Long[] purchaseIds);
}
