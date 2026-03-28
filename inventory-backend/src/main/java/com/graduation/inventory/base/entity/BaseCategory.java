package com.graduation.inventory.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.graduation.inventory.common.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 商品分类实体类
 *
 * @author graduation
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_category")
@ApiModel(description = "商品分类")
public class BaseCategory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 父分类ID
     */
    @ApiModelProperty(value = "父分类ID")
    private Long parentId;

    /**
     * 分类编码
     */
    @ApiModelProperty(value = "分类编码", required = true)
    @NotBlank(message = "分类编码不能为空")
    private String categoryCode;

    /**
     * 分类名称
     */
    @ApiModelProperty(value = "分类名称", required = true)
    @NotBlank(message = "分类名称不能为空")
    private String categoryName;

    /**
     * 显示顺序
     */
    @ApiModelProperty(value = "显示顺序")
    private Integer orderNum;

    /**
     * 状态(0禁用 1启用)
     */
    @ApiModelProperty(value = "状态(0禁用 1启用)")
    private Integer status;

    /**
     * 删除标志(0未删除 1已删除)
     */
    @ApiModelProperty(value = "删除标志", hidden = true)
    @TableField("is_deleted")
    private Integer isDeleted;

    /**
     * 子分类列表（非数据库字段）
     */
    @ApiModelProperty(value = "子分类列表")
    @TableField(exist = false)
    private List<BaseCategory> children;
}
