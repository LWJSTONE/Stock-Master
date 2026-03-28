package com.graduation.inventory.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.graduation.inventory.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 实时库存实体类
 *
 * @author graduation
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("stock_main")
public class StockMain extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 仓库ID
     */
    @TableField("warehouse_id")
    private Long warehouseId;

    /**
     * SKU ID
     */
    @TableField("sku_id")
    private Long skuId;

    /**
     * 可用库存
     */
    @TableField("quantity")
    private BigDecimal quantity;

    /**
     * 冻结库存
     */
    @TableField("frozen_qty")
    private BigDecimal frozenQty;

    /**
     * 批次号
     */
    @TableField("batch_no")
    private String batchNo;

    /**
     * 库位
     */
    @TableField("position")
    private String position;
}
