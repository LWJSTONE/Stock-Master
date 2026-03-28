package com.graduation.inventory.common.exception;

import com.graduation.inventory.common.constant.HttpStatus;
import java.io.Serializable;

/**
 * 自定义业务异常
 *
 * @author graduation
 * @version 1.0.0
 */
public class ServiceException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 消息
     */
    private String message;

    /**
     * 构造函数
     */
    public ServiceException() {
        this.code = HttpStatus.ERROR;
        this.message = "操作失败";
    }

    /**
     * 构造函数
     *
     * @param message 消息
     */
    public ServiceException(String message) {
        this.code = HttpStatus.ERROR;
        this.message = message;
    }

    /**
     * 构造函数
     *
     * @param code    状态码
     * @param message 消息
     */
    public ServiceException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 构造函数
     *
     * @param message 消息
     * @param cause   原因
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.code = HttpStatus.ERROR;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ServiceException{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
