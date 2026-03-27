package com.graduation.inventory.common.domain;

import com.graduation.inventory.common.constant.HttpStatus;
import java.io.Serializable;

/**
 * 统一响应结果类
 *
 * @param <T> 数据类型
 * @author graduation
 * @version 1.0.0
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 消息
     */
    private String msg;

    /**
     * 数据
     */
    private T data;

    public Result() {
    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 返回成功结果
     *
     * @param <T> 数据类型
     * @return 成功结果
     */
    public static <T> Result<T> success() {
        return new Result<>(HttpStatus.SUCCESS, "操作成功", null);
    }

    /**
     * 返回成功结果
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return 成功结果
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(HttpStatus.SUCCESS, "操作成功", data);
    }

    /**
     * 返回成功结果
     *
     * @param msg  消息
     * @param data 数据
     * @param <T>  数据类型
     * @return 成功结果
     */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(HttpStatus.SUCCESS, msg, data);
    }

    /**
     * 返回错误结果
     *
     * @param <T> 数据类型
     * @return 错误结果
     */
    public static <T> Result<T> error() {
        return new Result<>(HttpStatus.ERROR, "操作失败", null);
    }

    /**
     * 返回错误结果
     *
     * @param msg 消息
     * @param <T> 数据类型
     * @return 错误结果
     */
    public static <T> Result<T> error(String msg) {
        return new Result<>(HttpStatus.ERROR, msg, null);
    }

    /**
     * 返回错误结果
     *
     * @param code 状态码
     * @param msg  消息
     * @param <T>  数据类型
     * @return 错误结果
     */
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
