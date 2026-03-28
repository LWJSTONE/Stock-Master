package com.graduation.inventory.monitor.controller;

import com.graduation.inventory.common.annotation.Log;
import com.graduation.inventory.common.domain.PageResult;
import com.graduation.inventory.common.domain.Result;
import com.graduation.inventory.common.enums.BusinessType;
import com.graduation.inventory.system.entity.SysOperLog;
import com.graduation.inventory.system.service.SysOperLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 操作日志控制器
 *
 * @author graduation
 * @version 1.0.0
 */
@Api(tags = "操作日志")
@RestController
@RequestMapping("/monitor/operlog")
@RequiredArgsConstructor
public class SysOperLogController {

    private final SysOperLogService sysOperLogService;

    /**
     * 分页查询操作日志
     *
     * @param pageNum   页码
     * @param pageSize  每页数量
     * @param title     模块标题
     * @param operName  操作人员
     * @param status    操作状态
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 操作日志列表
     */
    @ApiOperation("分页查询操作日志")
    @GetMapping("/list")
    public Result<PageResult<SysOperLog>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("模块标题") @RequestParam(required = false) String title,
            @ApiParam("操作人员") @RequestParam(required = false) String operName,
            @ApiParam("操作状态") @RequestParam(required = false) Integer status,
            @ApiParam("开始时间") @RequestParam(required = false) String startTime,
            @ApiParam("结束时间") @RequestParam(required = false) String endTime) {
        SysOperLog operLog = new SysOperLog();
        operLog.setTitle(title);
        operLog.setOperName(operName);
        operLog.setStatus(status);
        List<SysOperLog> list = sysOperLogService.selectOperLogList(operLog);
        // 手动分页
        int start = (pageNum - 1) * pageSize;
        List<SysOperLog> pageList;
        if (list == null || list.isEmpty()) {
            pageList = new ArrayList<>();
        } else if (start >= list.size()) {
            pageList = new ArrayList<>();
        } else {
            int end = Math.min(start + pageSize, list.size());
            pageList = list.subList(start, end);
        }
        return Result.success(new PageResult<>(pageList, list != null ? (long) list.size() : 0L));
    }

    /**
     * 删除日志
     *
     * @param ids 日志ID列表
     * @return 结果
     */
    @ApiOperation("删除日志")
    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
    @Log(title = "操作日志", action = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Result<Void> remove(@ApiParam("日志ID列表") @PathVariable Long[] ids) {
        return sysOperLogService.deleteOperLogByIds(ids) > 0 ? Result.success() : Result.error("删除日志失败");
    }

    /**
     * 清空日志
     *
     * @return 结果
     */
    @ApiOperation("清空日志")
    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
    @Log(title = "操作日志", action = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public Result<Void> clean() {
        sysOperLogService.cleanOperLog();
        return Result.success();
    }

    /**
     * 导出日志
     *
     * @return 结果
     */
    @ApiOperation("导出日志")
    @PreAuthorize("@ss.hasPermi('monitor:operlog:export')")
    @Log(title = "操作日志", action = BusinessType.EXPORT)
    @PostMapping("/export")
    public Result<Void> export() {
        // TODO: 实现导出功能
        return Result.success("导出成功", null);
    }
}
