package com.graduation.inventory.business.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 审核DTO
 *
 * @author graduation
 * @version 1.0.0
 */
@Data
@ApiModel("审核请求")
public class AuditDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @ApiModelProperty(value = "订单ID", required = true)
    @NotNull(message = "订单ID不能为空")
    private Long id;

    /**
     * 审核状态（1通过 2驳回）
     */
    @ApiModelProperty(value = "审核状态", required = true)
    @NotNull(message = "审核状态不能为空")
    private Integer status;

    /**
     * 审核意见
     */
    @ApiModelProperty("审核意见")
    private String auditRemark;
}
