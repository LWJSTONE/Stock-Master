package com.graduation.inventory.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.inventory.base.entity.BaseCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品分类 Mapper接口
 *
 * @author graduation
 * @version 1.0.0
 */
@Mapper
public interface BaseCategoryMapper extends BaseMapper<BaseCategory> {
}
