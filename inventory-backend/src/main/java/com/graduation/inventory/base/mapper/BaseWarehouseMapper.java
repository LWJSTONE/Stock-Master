package com.graduation.inventory.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.inventory.base.entity.BaseWarehouse;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓库 Mapper接口
 *
 * @author graduation
 * @version 1.0.0
 */
@Mapper
public interface BaseWarehouseMapper extends BaseMapper<BaseWarehouse> {
}
