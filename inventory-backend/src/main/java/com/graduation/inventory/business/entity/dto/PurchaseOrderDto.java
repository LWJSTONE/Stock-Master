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
 * 采购订单DTO
 *
 * @author graduation
 * @version 1.0.0
 */
@Data
@ApiModel("采购订单")
public class PurchaseOrderDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ApiModelProperty("主键ID")
    private Long id;

    /**
     * 采购单号
     */
    @ApiModelProperty("采购单号")
    private String purchaseNo;

    /**
     * 供应商ID
     */
    @ApiModelProperty(value = "供应商ID", required = true)
    @NotNull(message = "供应商不能为空")
    private Long supplierId;

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
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 采购明细列表
     */
    @ApiModelProperty(value = "采购明细列表", required = true)
    @Valid
    @NotNull(message = "采购明细不能为空")
    private List<PurchaseItemDto> items;
}
