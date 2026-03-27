package com.graduation.inventory.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.inventory.base.entity.BaseCustomer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户 Mapper接口
 *
 * @author graduation
 * @version 1.0.0
 */
@Mapper
public interface BaseCustomerMapper extends BaseMapper<BaseCustomer> {
}
