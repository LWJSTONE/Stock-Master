package com.graduation.inventory.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 实时库存实体类
 *
 * @author graduation
 * @version 1.0.0
 */
@Data
@TableName("stock_main")
public class StockMain implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId
    private Long id;

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

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;
}
