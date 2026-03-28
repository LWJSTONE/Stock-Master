package com.graduation.inventory.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.graduation.inventory.business.entity.BusSaleOrder;
import com.graduation.inventory.business.entity.dto.AuditDto;
import com.graduation.inventory.business.entity.dto.SaleOrderDto;

/**
 * 销售订单服务接口
 *
 * @author graduation
 * @version 1.0.0
 */
public interface BusSaleOrderService extends IService<BusSaleOrder> {

    /**
     * 分页查询销售订单列表
     *
     * @param page       分页对象
     * @param saleNo     销售单号
     * @param customerId 客户ID
     * @param status     状态
     * @return 分页结果
     */
    Page<BusSaleOrder> selectSalePage(Page<BusSaleOrder> page, String saleNo, Long customerId, Integer status);

    /**
     * 根据ID查询销售订单详情
     *
     * @param saleId 销售订单ID
     * @return 销售订单详情
     */
    BusSaleOrder selectSaleById(Long saleId);

    /**
     * 新增销售订单
     *
     * @param dto 销售订单DTO
     * @return 是否成功
     */
    boolean insertSale(SaleOrderDto dto);

    /**
     * 审核销售订单
     *
     * @param dto 审核DTO
     * @return 是否成功
     */
    boolean auditSale(AuditDto dto);

    /**
     * 执行出库
     *
     * @param saleId 销售订单ID
     * @return 是否成功
     */
    boolean outstock(Long saleId);

    /**
     * 取消订单
     *
     * @param saleId 销售订单ID
     * @return 是否成功
     */
    boolean cancelSale(Long saleId);

    /**
     * 批量删除销售订单
     *
     * @param saleIds 销售订单ID数组
     * @return 是否成功
     */
    boolean deleteSaleByIds(Long[] saleIds);
}
