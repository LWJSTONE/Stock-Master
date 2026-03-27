package com.graduation.inventory.system.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 修改状态DTO
 *
 * @author graduation
 * @version 1.0.0
 */
@Data
@ApiModel("修改状态请求")
public class UserStatusDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID", required = true)
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 状态（0正常 1停用）
     */
    @ApiModelProperty(value = "状态（0正常 1停用）", required = true)
    @NotBlank(message = "状态不能为空")
    private String status;
}
