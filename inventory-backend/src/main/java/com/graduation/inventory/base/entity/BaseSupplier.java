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
 * 供应商实体类
 *
 * @author graduation
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_supplier")
@ApiModel(description = "供应商信息")
public class BaseSupplier extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 供应商编码
     */
    @ApiModelProperty(value = "供应商编码", required = true)
    @NotBlank(message = "供应商编码不能为空")
    private String supCode;

    /**
     * 供应商名称
     */
    @ApiModelProperty(value = "供应商名称", required = true)
    @NotBlank(message = "供应商名称不能为空")
    private String supName;

    /**
     * 联系人
     */
    @ApiModelProperty(value = "联系人")
    private String contactPerson;

    /**
     * 联系电话
     */
    @ApiModelProperty(value = "联系电话")
    private String phone;

    /**
     * 地址
     */
    @ApiModelProperty(value = "地址")
    private String address;

    /**
     * 银行账号
     */
    @ApiModelProperty(value = "银行账号")
    private String bankAccount;

    /**
     * 删除标志(0未删除 1已删除)
     */
    @ApiModelProperty(value = "删除标志", hidden = true)
    @TableLogic(value = "0", delval = "1")
    @TableField("is_deleted")
    private Integer isDeleted;
}
