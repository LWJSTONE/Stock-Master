package com.graduation.inventory.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 库存流水实体类
 *
 * @author graduation
 * @version 1.0.0
 */
@Data
@TableName("stock_record")
public class StockRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 订单号
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 订单类型（1采购入库 2销售出库 3调拨入 4调拨出 5盘盈 6盘亏 7报损）
     */
    @TableField("order_type")
    private Integer orderType;

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
     * SKU编码（非数据库字段，用于显示）
     */
    @TableField(exist = false)
    private String skuCode;

    /**
     * SKU名称（非数据库字段，用于显示）
     */
    @TableField(exist = false)
    private String skuName;

    /**
     * 变动数量
     */
    @TableField("change_qty")
    private BigDecimal changeQty;

    /**
     * 变动前数量
     */
    @TableField("before_qty")
    private BigDecimal beforeQty;

    /**
     * 变动后数量
     */
    @TableField("after_qty")
    private BigDecimal afterQty;

    /**
     * 批次号
     */
    @TableField("batch_no")
    private String batchNo;

    /**
     * 供应商ID
     */
    @TableField("supplier_id")
    private Long supplierId;

    /**
     * 客户ID
     */
    @TableField("customer_id")
    private Long customerId;

    /**
     * 操作人ID
     */
    @TableField("operator_id")
    private Long operatorId;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
}
