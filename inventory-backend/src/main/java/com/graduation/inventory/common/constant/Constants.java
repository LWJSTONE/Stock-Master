package com.graduation.inventory.common.constant;

/**
 * 通用常量定义
 *
 * @author graduation
 * @version 1.0.0
 */
public class Constants {

    /**
     * 成功标记
     */
    public static final Integer SUCCESS = 200;

    /**
     * 失败标记
     */
    public static final Integer ERROR = 500;

    /**
     * 未授权
     */
    public static final Integer UNAUTHORIZED = 401;

    /**
     * 禁止访问
     */
    public static final Integer FORBIDDEN = 403;

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "Success";

    /**
     * 登录失败
     */
    public static final String LOGIN_FAIL = "Error";

    /**
     * 验证码有效期（分钟）
     */
    public static final Integer CAPTCHA_EXPIRATION = 2;

    /**
     * 令牌
     */
    public static final String TOKEN = "token";

    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 令牌请求头
     */
    public static final String HEADER = "Authorization";

    private Constants() {
    }
}
