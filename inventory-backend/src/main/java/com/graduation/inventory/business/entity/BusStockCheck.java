package com.graduation.inventory.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.graduation.inventory.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 库存盘点主表实体类
 *
 * @author graduation
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bus_stock_check")
public class BusStockCheck extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 盘点单号
     */
    @TableField("check_no")
    private String checkNo;

    /**
     * 仓库ID
     */
    @TableField("warehouse_id")
    private Long warehouseId;

    /**
     * 盘点状态（0盘点中 1完成）
     */
    @TableField("check_status")
    private Integer checkStatus;

    /**
     * 盘点时间
     */
    @TableField("check_time")
    private Date checkTime;

    /**
     * 删除标志(0未删除 1已删除)
     */
    @TableLogic(value = "0", delval = "1")
    @TableField("is_deleted")
    private Integer isDeleted;
}
