package com.graduation.inventory.monitor.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 分类库存占比VO
 *
 * @author graduation
 * @version 1.0.0
 */
@Data
@ApiModel(description = "分类库存占比")
public class CategoryRatioVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类名称
     */
    @ApiModelProperty(value = "分类名称")
    private String categoryName;

    /**
     * 库存数量
     */
    @ApiModelProperty(value = "库存数量")
    private BigDecimal quantity;

    /**
     * 占比
     */
    @ApiModelProperty(value = "占比")
    private BigDecimal ratio;
}
