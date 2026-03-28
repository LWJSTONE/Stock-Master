package com.graduation.inventory.business.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 采购明细DTO
 *
 * @author graduation
 * @version 1.0.0
 */
@Data
@ApiModel("采购明细")
public class PurchaseItemDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ApiModelProperty("主键ID")
    private Long id;

    /**
     * SKU ID
     */
    @ApiModelProperty(value = "SKU ID", required = true)
    @NotNull(message = "商品不能为空")
    private Long skuId;

    /**
     * SKU编码
     */
    @ApiModelProperty("SKU编码")
    private String skuCode;

    /**
     * SKU名称
     */
    @ApiModelProperty("SKU名称")
    private String skuName;

    /**
     * 单价
     */
    @ApiModelProperty(value = "单价", required = true)
    @NotNull(message = "单价不能为空")
    @DecimalMin(value = "0.01", message = "单价必须大于0")
    private BigDecimal price;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量", required = true)
    @NotNull(message = "数量不能为空")
    @DecimalMin(value = "0.01", message = "数量必须大于0")
    private BigDecimal quantity;

    /**
     * 总价
     */
    @ApiModelProperty("总价")
    private BigDecimal totalPrice;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;
}
