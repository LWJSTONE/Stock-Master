package com.graduation.inventory.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 盘点明细实体类
 *
 * @author graduation
 * @version 1.0.0
 */
@Data
@TableName("bus_stock_check_item")
public class BusStockCheckItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 盘点主表ID
     */
    @TableField("check_id")
    private Long checkId;

    /**
     * SKU ID
     */
    @TableField("sku_id")
    private Long skuId;

    /**
     * SKU编码（非数据库字段，用于前端显示）
     */
    @TableField(exist = false)
    private String skuCode;

    /**
     * SKU名称（非数据库字段，用于前端显示）
     */
    @TableField(exist = false)
    private String skuName;

    /**
     * 规格信息（非数据库字段，用于前端显示）
     */
    @TableField(exist = false)
    private String specValues;

    /**
     * 单位（非数据库字段，用于前端显示）
     */
    @TableField(exist = false)
    private String unit;

    /**
     * 单价（非数据库字段，用于前端显示）
     */
    @TableField(exist = false)
    private BigDecimal price;

    /**
     * 系统数量
     */
    @TableField("system_qty")
    private BigDecimal systemQty;

    /**
     * 实际数量
     */
    @TableField("actual_qty")
    private BigDecimal actualQty;

    /**
     * 差异数量
     */
    @TableField("diff_qty")
    private BigDecimal diffQty;

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

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;
}
