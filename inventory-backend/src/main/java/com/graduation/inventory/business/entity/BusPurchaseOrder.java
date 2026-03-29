package com.graduation.inventory.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.graduation.inventory.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 采购订单主表实体类
 *
 * @author graduation
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bus_purchase_order")
public class BusPurchaseOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 采购单号
     */
    @TableField("purchase_no")
    private String purchaseNo;

    /**
     * 供应商ID
     */
    @TableField("supplier_id")
    private Long supplierId;

    /**
     * 总金额
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 状态（0待审核 1已审核 2已入库 3已取消）
     */
    @TableField("status")
    private Integer status;

    /**
     * 申请人ID
     */
    @TableField("applicant_id")
    private Long applicantId;

    /**
     * 审核时间
     */
    @TableField("audit_time")
    private Date auditTime;

    /**
     * 审核人
     */
    @TableField("audit_by")
    private Long auditBy;

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

    /**
     * 采购明细列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<BusPurchaseItem> items;
}
