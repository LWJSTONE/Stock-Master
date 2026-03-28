package com.graduation.inventory.system.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 分配菜单权限DTO
 *
 * @author graduation
 * @version 1.0.0
 */
@Data
@ApiModel("分配菜单权限请求")
public class AllotMenuDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @ApiModelProperty(value = "角色ID", required = true)
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    /**
     * 菜单ID列表
     */
    @ApiModelProperty("菜单ID列表")
    private List<Long> menuIds;
}
