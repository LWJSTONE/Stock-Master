package com.graduation.inventory.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.graduation.inventory.common.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * 仓库实体类
 *
 * @author graduation
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_warehouse")
@ApiModel(description = "仓库信息")
public class BaseWarehouse extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 仓库编码
     */
    @ApiModelProperty(value = "仓库编码", required = true)
    @NotBlank(message = "仓库编码不能为空")
    private String whCode;

    /**
     * 仓库名称
     */
    @ApiModelProperty(value = "仓库名称", required = true)
    @NotBlank(message = "仓库名称不能为空")
    private String whName;

    /**
     * 仓库地址
     */
    @ApiModelProperty(value = "仓库地址")
    private String address;

    /**
     * 负责人
     */
    @ApiModelProperty(value = "负责人")
    private String principal;
    
    /**
     * 联系电话
     */
    @ApiModelProperty(value = "联系电话")
    private String phone;
    
    /**
     * 仓库面积
     */
    @ApiModelProperty(value = "仓库面积")
    private String area;
    
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 状态(0禁用 1启用)
     */
    @ApiModelProperty(value = "状态(0禁用 1启用)")
    private Integer status;

    /**
     * 删除标志(0未删除 1已删除)
     */
    @ApiModelProperty(value = "删除标志", hidden = true)
    @TableLogic(value = "0", delval = "1")
    @TableField("is_deleted")
    private Integer isDeleted;
}
