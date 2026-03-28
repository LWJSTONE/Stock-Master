package com.graduation.inventory.monitor.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 大屏概览数据VO
 *
 * @author graduation
 * @version 1.0.0
 */
@Data
@ApiModel(description = "大屏概览数据")
public class OverviewVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总库存
     */
    @ApiModelProperty(value = "总库存")
    private BigDecimal totalStock;

    /**
     * SKU数量
     */
    @ApiModelProperty(value = "SKU数量")
    private Integer totalSku;

    /**
     * 待审核采购单
     */
    @ApiModelProperty(value = "待审核采购单")
    private Integer pendingPurchase;

    /**
     * 待审核销售单
     */
    @ApiModelProperty(value = "待审核销售单")
    private Integer pendingSale;

    /**
     * 今日入库
     */
    @ApiModelProperty(value = "今日入库")
    private BigDecimal todayInbound;

    /**
     * 今日出库
     */
    @ApiModelProperty(value = "今日出库")
    private BigDecimal todayOutbound;

    /**
     * 待审核订单总数（采购单+销售单）
     */
    @ApiModelProperty(value = "待审核订单总数")
    private Integer pendingOrders;
}
