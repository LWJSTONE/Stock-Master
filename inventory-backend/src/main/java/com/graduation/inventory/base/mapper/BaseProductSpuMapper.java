package com.graduation.inventory.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.inventory.base.entity.BaseProductSpu;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品SPU Mapper接口
 *
 * @author graduation
 * @version 1.0.0
 */
@Mapper
public interface BaseProductSpuMapper extends BaseMapper<BaseProductSpu> {
}
