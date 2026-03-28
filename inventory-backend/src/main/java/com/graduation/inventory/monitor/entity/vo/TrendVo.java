package com.graduation.inventory.monitor.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 出入库趋势数据VO
 *
 * @author graduation
 * @version 1.0.0
 */
@Data
@ApiModel(description = "出入库趋势数据")
public class TrendVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日期
     */
    @ApiModelProperty(value = "日期")
    private String date;

    /**
     * 入库数量
     */
    @ApiModelProperty(value = "入库数量")
    private BigDecimal inbound;

    /**
     * 出库数量
     */
    @ApiModelProperty(value = "出库数量")
    private BigDecimal outbound;
}
