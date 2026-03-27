package com.graduation.inventory.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.inventory.system.entity.SysOperLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志Mapper接口
 * 
 * @author graduation
 * @version 1.0.0
 */
@Mapper
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {

}
