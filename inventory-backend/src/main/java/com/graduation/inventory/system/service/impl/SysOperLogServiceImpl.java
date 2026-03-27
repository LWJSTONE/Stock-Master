package com.graduation.inventory.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.inventory.common.utils.StringUtils;
import com.graduation.inventory.system.entity.SysOperLog;
import com.graduation.inventory.system.mapper.SysOperLogMapper;
import com.graduation.inventory.system.service.SysOperLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 操作日志服务实现
 * 
 * @author graduation
 * @version 1.0.0
 */
@Service
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog> implements SysOperLogService {

    private static final Logger log = LoggerFactory.getLogger(SysOperLogServiceImpl.class);

    @Autowired
    private SysOperLogMapper operLogMapper;

    /**
     * 新增操作日志
     *
     * @param operLog 操作日志信息
     * @return 影响行数
     */
    @Override
    @Async
    public int insertOperLog(SysOperLog operLog) {
        if (operLog == null) {
            return 0;
        }

        // 设置操作时间
        if (operLog.getOperTime() == null) {
            operLog.setOperTime(new Date());
        }

        // 设置默认状态
        if (operLog.getStatus() == null) {
            operLog.setStatus(0);
        }

        int result = operLogMapper.insert(operLog);
        log.debug("新增操作日志成功, 模块: {}", operLog.getTitle());

        return result;
    }

    /**
     * 分页查询日志列表
     *
     * @param operLog 操作日志查询条件
     * @return 日志列表
     */
    @Override
    public List<SysOperLog> selectOperLogList(SysOperLog operLog) {
        LambdaQueryWrapper<SysOperLog> queryWrapper = new LambdaQueryWrapper<>();

        // 根据模块标题模糊查询
        if (StringUtils.isNotBlank(operLog.getTitle())) {
            queryWrapper.like(SysOperLog::getTitle, operLog.getTitle());
        }
        // 根据操作人员模糊查询
        if (StringUtils.isNotBlank(operLog.getOperName())) {
            queryWrapper.like(SysOperLog::getOperName, operLog.getOperName());
        }
        // 根据操作类型精确查询
        if (StringUtils.isNotBlank(operLog.getRequestMethod())) {
            queryWrapper.eq(SysOperLog::getRequestMethod, operLog.getRequestMethod());
        }
        // 根据操作状态精确查询
        if (operLog.getStatus() != null) {
            queryWrapper.eq(SysOperLog::getStatus, operLog.getStatus());
        }
        // 按操作时间倒序
        queryWrapper.orderByDesc(SysOperLog::getOperTime);

        return operLogMapper.selectList(queryWrapper);
    }

    /**
     * 批量删除操作日志
     *
     * @param operIds 日志ID数组
     * @return 影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteOperLogByIds(Long[] operIds) {
        if (operIds == null || operIds.length == 0) {
            return 0;
        }

        int result = operLogMapper.deleteBatchIds(Arrays.asList(operIds));
        log.info("批量删除操作日志成功, 日志ID: {}", Arrays.toString(operIds));

        return result;
    }

    /**
     * 清空操作日志
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cleanOperLog() {
        operLogMapper.delete(new LambdaQueryWrapper<>());
        log.info("清空操作日志成功");
    }
}
