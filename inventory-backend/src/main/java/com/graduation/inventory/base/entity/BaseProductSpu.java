package com.graduation.inventory.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.graduation.inventory.common.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * 商品标准单元实体类(SPU)
 *
 * @author graduation
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_product_spu")
@ApiModel(description = "商品标准单元(SPU)")
public class BaseProductSpu extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * SPU编码
     */
    @ApiModelProperty(value = "SPU编码", required = true)
    @NotBlank(message = "SPU编码不能为空")
    private String spuCode;

    /**
     * SPU名称
     */
    @ApiModelProperty(value = "SPU名称", required = true)
    @NotBlank(message = "SPU名称不能为空")
    private String spuName;

    /**
     * 分类ID
     */
    @ApiModelProperty(value = "分类ID")
    private Long categoryId;

    /**
     * 品牌
     */
    @ApiModelProperty(value = "品牌")
    private String brand;

    /**
     * 单位
     */
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * 状态(0禁用 1启用)
     */
    @ApiModelProperty(value = "状态(0禁用 1启用)")
    private Integer status;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * 删除标志(0未删除 1已删除)
     */
    @ApiModelProperty(value = "删除标志", hidden = true)
    @TableField("is_deleted")
    private Integer isDeleted;

    /**
     * SKU列表（非数据库字段）
     */
    @ApiModelProperty(value = "SKU列表")
    @TableField(exist = false)
    private java.util.List<BaseProductSku> skuList;
}
