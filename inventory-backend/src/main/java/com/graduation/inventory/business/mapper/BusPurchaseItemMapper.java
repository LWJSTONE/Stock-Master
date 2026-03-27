package com.graduation.inventory.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.inventory.business.entity.BusPurchaseItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 采购订单明细Mapper接口
 *
 * @author graduation
 * @version 1.0.0
 */
@Mapper
public interface BusPurchaseItemMapper extends BaseMapper<BusPurchaseItem> {

}
