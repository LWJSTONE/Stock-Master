package com.graduation.inventory.business.entity.dto;

import com.graduation.inventory.business.entity.BusStockCheckItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 盘点DTO
 *
 * @author graduation
 * @version 1.0.0
 */
@Data
@ApiModel("盘点单")
public class StockCheckDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ApiModelProperty("主键ID")
    private Long id;

    /**
     * 盘点单号
     */
    @ApiModelProperty("盘点单号")
    private String checkNo;

    /**
     * 仓库ID
     */
    @ApiModelProperty(value = "仓库ID", required = true)
    @NotNull(message = "仓库不能为空")
    private Long warehouseId;

    /**
     * 盘点状态
     */
    @ApiModelProperty("盘点状态")
    private Integer checkStatus;

    /**
     * SKU ID列表（选择参与盘点的商品）
     */
    @ApiModelProperty(value = "SKU ID列表", required = true)
    @NotNull(message = "请选择要盘点的商品")
    private List<Long> skuIds;

    /**
     * 盘点明细列表（批量提交实盘数量时使用）
     */
    @ApiModelProperty("盘点明细列表")
    private List<BusStockCheckItem> items;

    /**
     * 盘点明细ID（提交实盘数量时使用）
     */
    @ApiModelProperty("盘点明细ID")
    private Long itemId;

    /**
     * 实际数量（提交实盘数量时使用）
     */
    @ApiModelProperty("实际数量")
    private BigDecimal actualQty;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;
}
