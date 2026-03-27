package com.graduation.inventory.system.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 重置密码DTO
 *
 * @author graduation
 * @version 1.0.0
 */
@Data
@ApiModel("重置密码请求")
public class ResetPwdDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID", required = true)
    private Long userId;

    /**
     * 新密码
     */
    @ApiModelProperty(value = "新密码", required = true)
    @NotBlank(message = "新密码不能为空")
    private String password;
}
