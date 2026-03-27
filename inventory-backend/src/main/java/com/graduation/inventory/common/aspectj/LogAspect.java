package com.graduation.inventory.common.aspectj;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graduation.inventory.common.annotation.Log;
import com.graduation.inventory.common.utils.ServletUtil;
import com.graduation.inventory.common.utils.StringUtils;
import com.graduation.inventory.security.LoginUser;
import com.graduation.inventory.system.entity.SysOperLog;
import com.graduation.inventory.system.service.SysOperLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 操作日志AOP切面
 *
 * @author graduation
 * @version 1.0.0
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    /**
     * 操作日志服务
     */
    private final SysOperLogService operLogService;

    /**
     * JSON工具
     */
    private final ObjectMapper objectMapper;

    /**
     * 请求开始时间（使用ThreadLocal存储）
     */
    private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();

    /**
     * 方法执行前，记录开始时间
     *
     * @param joinPoint 切点
     * @param logAnnotation 日志注解
     */
    @Before("@annotation(logAnnotation)")
    public void doBefore(JoinPoint joinPoint, Log logAnnotation) {
        START_TIME.set(System.currentTimeMillis());
    }

    /**
     * 方法执行后，处理日志记录
     *
     * @param joinPoint       切点
     * @param logAnnotation   日志注解
     * @param jsonResult      返回结果
     */
    @AfterReturning(pointcut = "@annotation(logAnnotation)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Log logAnnotation, Object jsonResult) {
        handleLog(joinPoint, logAnnotation, null, jsonResult);
    }

    /**
     * 方法抛出异常后，处理日志记录
     *
     * @param joinPoint     切点
     * @param logAnnotation 日志注解
     * @param e             异常
     */
    @AfterThrowing(pointcut = "@annotation(logAnnotation)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Log logAnnotation, Exception e) {
        handleLog(joinPoint, logAnnotation, e, null);
    }

    /**
     * 处理日志记录
     *
     * @param joinPoint     切点
     * @param logAnnotation 日志注解
     * @param e             异常
     * @param jsonResult    返回结果
     */
    private void handleLog(JoinPoint joinPoint, Log logAnnotation, Exception e, Object jsonResult) {
        try {
            // 获取当前登录用户
            String operName = getOperName();
            
            // 构建操作日志对象
            SysOperLog operLog = new SysOperLog();
            operLog.setStatus(0); // 默认正常
            operLog.setOperName(operName);
            operLog.setOperIp(ServletUtil.getClientIP());
            operLog.setOperUrl(StringUtils.substring(ServletUtil.getRequestURL(), 0, 255));
            operLog.setRequestMethod(ServletUtil.getMethod());
            operLog.setOperTime(new Date());
            
            // 计算执行时间
            Long startTime = START_TIME.get();
            if (startTime != null) {
                operLog.setCostTime(System.currentTimeMillis() - startTime);
                START_TIME.remove();
            }
            
            // 设置日志注解信息
            if (logAnnotation != null) {
                operLog.setTitle(logAnnotation.title());
                operLog.setMethod(joinPoint.getSignature().getName());
                
                // 设置业务类型
                if (logAnnotation.action() != null) {
                    operLog.setMethod(operLog.getMethod() + " [" + logAnnotation.action().getDescription() + "]");
                }
                
                // 保存请求参数
                if (logAnnotation.isSaveRequestData()) {
                    setRequestValue(joinPoint, operLog);
                }
                
                // 保存响应结果
                if (logAnnotation.isSaveResponseData() && jsonResult != null) {
                    String resultStr = objectMapper.writeValueAsString(jsonResult);
                    operLog.setJsonResult(StringUtils.substring(resultStr, 0, 2000));
                }
            }
            
            // 处理异常
            if (e != null) {
                operLog.setStatus(1); // 异常状态
                String errorMsg = e.getMessage();
                operLog.setErrorMsg(StringUtils.substring(errorMsg, 0, 2000));
            }
            
            // 异步保存操作日志
            saveOperLog(operLog);
            
        } catch (Exception ex) {
            log.error("记录操作日志异常", ex);
        }
    }

    /**
     * 获取当前操作用户名称
     *
     * @return 用户名称
     */
    private String getOperName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            return loginUser.getUsername();
        }
        return "unknown";
    }

    /**
     * 获取请求参数
     *
     * @param joinPoint 切点
     * @param operLog   操作日志
     */
    private void setRequestValue(JoinPoint joinPoint, SysOperLog operLog) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                // 过滤掉不可序列化的参数（如HttpServletRequest、HttpServletResponse）
                StringBuilder params = new StringBuilder();
                for (Object arg : args) {
                    if (arg != null && !isIgnoreClass(arg.getClass())) {
                        String argStr = objectMapper.writeValueAsString(arg);
                        params.append(argStr).append(",");
                    }
                }
                if (params.length() > 0) {
                    params.deleteCharAt(params.length() - 1);
                }
                operLog.setOperParam(StringUtils.substring(params.toString(), 0, 2000));
            }
        } catch (Exception e) {
            log.error("获取请求参数异常", e);
        }
    }

    /**
     * 判断是否忽略的类
     *
     * @param clazz 类
     * @return 是否忽略
     */
    private boolean isIgnoreClass(Class<?> clazz) {
        String className = clazz.getName();
        return className.contains("HttpServletRequest") 
                || className.contains("HttpServletResponse")
                || className.contains("HttpSession")
                || className.contains("MultipartFile")
                || className.contains("Servlet")
                || className.contains("InputStream")
                || className.contains("OutputStream");
    }

    /**
     * 异步保存操作日志
     *
     * @param operLog 操作日志
     */
    private void saveOperLog(SysOperLog operLog) {
        try {
            operLogService.insertOperLog(operLog);
        } catch (Exception e) {
            log.error("保存操作日志异常", e);
        }
    }
}
