package com.graduation.inventory.monitor.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 库存预警VO
 *
 * @author graduation
 * @version 1.0.0
 */
@Data
@ApiModel(description = "库存预警")
public class StockWarningVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * SKU ID
     */
    @ApiModelProperty(value = "SKU ID")
    private Long skuId;

    /**
     * SKU编码
     */
    @ApiModelProperty(value = "SKU编码")
    private String skuCode;

    /**
     * SKU名称
     */
    @ApiModelProperty(value = "SKU名称")
    private String skuName;

    /**
     * 当前库存
     */
    @ApiModelProperty(value = "当前库存")
    private BigDecimal currentStock;

    /**
     * 安全库存
     */
    @ApiModelProperty(value = "安全库存")
    private Integer safetyStock;

    /**
     * 预警类型（1库存不足 2库存过量）
     */
    @ApiModelProperty(value = "预警类型（1库存不足 2库存过量）")
    private Integer warningType;
}
