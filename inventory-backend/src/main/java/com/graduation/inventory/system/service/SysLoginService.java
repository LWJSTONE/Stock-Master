package com.graduation.inventory.system.service;

/**
 * 登录服务接口
 * 
 * @author graduation
 * @version 1.0.0
 */
public interface SysLoginService {

    /**
     * 登录验证并返回Token
     *
     * @param username 用户名
     * @param password 密码
     * @return Token字符串
     */
    String login(String username, String password);

    /**
     * 登出
     *
     * @param username 用户名
     */
    void logout(String username);
}
