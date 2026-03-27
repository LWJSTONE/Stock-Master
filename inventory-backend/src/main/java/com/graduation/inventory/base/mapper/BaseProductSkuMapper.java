package com.graduation.inventory.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.inventory.base.entity.BaseProductSku;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品SKU Mapper接口
 *
 * @author graduation
 * @version 1.0.0
 */
@Mapper
public interface BaseProductSkuMapper extends BaseMapper<BaseProductSku> {
}
