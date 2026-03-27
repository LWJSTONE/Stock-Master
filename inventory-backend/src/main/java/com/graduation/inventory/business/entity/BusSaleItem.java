package com.graduation.inventory.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 销售订单明细实体类
 *
 * @author graduation
 * @version 1.0.0
 */
@Data
@TableName("bus_sale_item")
public class BusSaleItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 销售订单ID
     */
    @TableField("sale_id")
    private Long saleId;

    /**
     * SKU ID
     */
    @TableField("sku_id")
    private Long skuId;

    /**
     * SKU编码
     */
    @TableField("sku_code")
    private String skuCode;

    /**
     * SKU名称
     */
    @TableField("sku_name")
    private String skuName;

    /**
     * 单价
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 数量
     */
    @TableField("quantity")
    private BigDecimal quantity;

    /**
     * 总价
     */
    @TableField("total_price")
    private BigDecimal totalPrice;

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
