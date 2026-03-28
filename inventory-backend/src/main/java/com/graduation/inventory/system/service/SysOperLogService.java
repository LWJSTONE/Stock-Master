package com.graduation.inventory.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.graduation.inventory.system.entity.SysOperLog;

import java.util.List;

/**
 * 操作日志服务接口
 * 
 * @author graduation
 * @version 1.0.0
 */
public interface SysOperLogService extends IService<SysOperLog> {

    /**
     * 新增操作日志
     *
     * @param operLog 操作日志信息
     */
    void insertOperLog(SysOperLog operLog);

    /**
     * 分页查询日志列表
     *
     * @param operLog 操作日志查询条件
     * @return 日志列表
     */
    List<SysOperLog> selectOperLogList(SysOperLog operLog);

    /**
     * 批量删除操作日志
     *
     * @param operIds 日志ID数组
     * @return 影响行数
     */
    int deleteOperLogByIds(Long[] operIds);

    /**
     * 清空操作日志
     */
    void cleanOperLog();
}
