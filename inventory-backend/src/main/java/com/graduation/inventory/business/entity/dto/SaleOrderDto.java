package com.graduation.inventory.business.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 销售订单DTO
 *
 * @author graduation
 * @version 1.0.0
 */
@Data
@ApiModel("销售订单")
public class SaleOrderDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ApiModelProperty("主键ID")
    private Long id;

    /**
     * 销售单号
     */
    @ApiModelProperty("销售单号")
    private String saleNo;

    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID", required = true)
    @NotNull(message = "客户不能为空")
    private Long customerId;

    /**
     * 总金额
     */
    @ApiModelProperty("总金额")
    private BigDecimal totalAmount;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private Integer status;

    /**
     * 支付状态
     */
    @ApiModelProperty("支付状态")
    private Integer payStatus;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 销售明细列表
     */
    @ApiModelProperty(value = "销售明细列表", required = true)
    @Valid
    @NotNull(message = "销售明细不能为空")
    private List<SaleItemDto> items;
}
