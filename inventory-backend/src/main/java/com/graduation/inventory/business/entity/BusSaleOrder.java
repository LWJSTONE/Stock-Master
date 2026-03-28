package com.graduation.inventory.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.graduation.inventory.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 销售订单主表实体类
 *
 * @author graduation
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bus_sale_order")
public class BusSaleOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 销售单号
     */
    @TableField("sale_no")
    private String saleNo;

    /**
     * 客户ID
     */
    @TableField("customer_id")
    private Long customerId;

    /**
     * 总金额
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 状态（0待审核 1已审核 2已出库 3已取消）
     */
    @TableField("status")
    private Integer status;

    /**
     * 支付状态
     */
    @TableField("pay_status")
    private Integer payStatus;

    /**
     * 发货时间
     */
    @TableField("deliver_time")
    private Date deliverTime;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 删除标志(0未删除 1已删除)
     */
    @TableLogic(value = "0", delval = "1")
    @TableField("is_deleted")
    private Integer isDeleted;
}
