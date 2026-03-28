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
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 商品库存单元实体类(SKU)
 *
 * @author graduation
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_product_sku")
@ApiModel(description = "商品库存单元(SKU)")
public class BaseProductSku extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * SPU ID
     */
    @ApiModelProperty(value = "SPU ID", required = true)
    @NotNull(message = "SPU ID不能为空")
    private Long spuId;

    /**
     * SKU编码
     */
    @ApiModelProperty(value = "SKU编码", required = true)
    @NotBlank(message = "SKU编码不能为空")
    private String skuCode;

    /**
     * SKU名称
     */
    @ApiModelProperty(value = "SKU名称", required = true)
    @NotBlank(message = "SKU名称不能为空")
    private String skuName;

    /**
     * 规格信息(JSON格式)
     */
    @ApiModelProperty(value = "规格信息(JSON格式)")
    @TableField("spec_info")
    private String specInfo;

    /**
     * 销售价格
     */
    @ApiModelProperty(value = "销售价格")
    private BigDecimal salePrice;

    /**
     * 成本价格
     */
    @ApiModelProperty(value = "成本价格")
    private BigDecimal costPrice;

    /**
     * 图片地址
     */
    @ApiModelProperty(value = "图片地址")
    private String imageUrl;

    /**
     * 安全库存
     */
    @ApiModelProperty(value = "安全库存")
    private Integer safetyStock;

    /**
     * 删除标志(0未删除 1已删除)
     */
    @ApiModelProperty(value = "删除标志", hidden = true)
    @TableLogic(value = "0", delval = "1")
    @TableField("is_deleted")
    private Integer isDeleted;
}
