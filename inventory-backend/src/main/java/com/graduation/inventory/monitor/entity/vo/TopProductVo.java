package com.graduation.inventory.monitor.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * TOP商品VO
 *
 * @author graduation
 * @version 1.0.0
 */
@Data
@ApiModel(description = "TOP商品")
public class TopProductVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    private String skuName;

    /**
     * 销售数量
     */
    @ApiModelProperty(value = "销售数量")
    private BigDecimal quantity;

    /**
     * 销售金额
     */
    @ApiModelProperty(value = "销售金额")
    private BigDecimal amount;
}
