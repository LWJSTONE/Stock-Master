package com.graduation.inventory.common.exception;

import com.graduation.inventory.common.constant.HttpStatus;
import com.graduation.inventory.common.domain.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author graduation
 * @version 1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理自定义业务异常
     *
     * @param e 业务异常
     * @return 结果
     */
    @ExceptionHandler(ServiceException.class)
    public Result<Void> handleServiceException(ServiceException e) {
        log.error("业务异常：{}", e.getMessage(), e);
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常（@RequestBody参数校验）
     *
     * @param e 参数校验异常
     * @return 结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("参数校验异常：{}", e.getMessage());
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Result.error(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 处理参数绑定异常（@ModelAttribute参数校验）
     *
     * @param e 参数绑定异常
     * @return 结果
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        log.error("参数绑定异常：{}", e.getMessage());
        String message = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Result.error(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 处理约束违规异常（@RequestParam参数校验）
     *
     * @param e 约束违规异常
     * @return 结果
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("约束违规异常：{}", e.getMessage());
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        return Result.error(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 处理权限异常
     *
     * @param e 权限异常
     * @return 结果
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Result<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.error("权限异常：{}", e.getMessage());
        return Result.error(HttpStatus.FORBIDDEN, "没有权限，请联系管理员授权");
    }

    /**
     * 处理运行时异常
     *
     * @param e 运行时异常
     * @return 结果
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常：{}", e.getMessage(), e);
        return Result.error("系统异常，请稍后重试");
    }

    /**
     * 处理其他异常
     *
     * @param e 异常
     * @return 结果
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常：{}", e.getMessage(), e);
        return Result.error("系统异常，请稍后重试");
    }
}
