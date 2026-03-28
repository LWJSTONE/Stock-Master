package com.graduation.inventory.system.service.impl;

import com.graduation.inventory.common.constant.Constants;
import com.graduation.inventory.common.exception.ServiceException;
import com.graduation.inventory.common.utils.JwtUtil;
import com.graduation.inventory.security.JwtProperties;
import com.graduation.inventory.security.LoginUser;
import com.graduation.inventory.system.service.SysLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录服务实现
 * 
 * @author graduation
 * @version 1.0.0
 */
@Service
public class SysLoginServiceImpl implements SysLoginService {

    private static final Logger log = LoggerFactory.getLogger(SysLoginServiceImpl.class);

    /**
     * 用户登录缓存（实际项目中应使用Redis）
     * key: username, value: LoginUser
     */
    private static final Map<String, LoginUser> loginUserCache = new ConcurrentHashMap<>();

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录验证并返回Token
     *
     * @param username 用户名
     * @param password 密码
     * @return Token字符串
     */
    @Override
    public String login(String username, String password) {
        log.info("用户登录: {}", username);

        // 用户验证
        Authentication authentication;
        try {
            // 使用Spring Security的AuthenticationManager进行认证
            UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(username, password);
            authentication = authenticationManager.authenticate(authToken);
        } catch (BadCredentialsException e) {
            log.error("用户登录失败，用户名或密码错误: {}", username);
            throw new ServiceException("用户名或密码错误");
        } catch (Exception e) {
            log.error("用户登录异常: {}", username, e);
            throw new ServiceException("登录失败，请稍后重试");
        }

        // 认证成功，获取登录用户信息
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        // 检查用户状态
        if ("1".equals(loginUser.getStatus())) {
            throw new ServiceException("用户已被停用，请联系管理员");
        }

        // 生成JWT Token
        String token = jwtUtil.generateToken(loginUser);

        // 设置登录时间
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + jwtProperties.getExpiration());
        loginUser.setToken(token);

        // 将用户信息存入缓存
        loginUserCache.put(username, loginUser);

        // 将认证信息存入SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("用户登录成功: {}", username);
        return token;
    }

    /**
     * 登出
     *
     * @param username 用户名
     */
    @Override
    public void logout(String username) {
        log.info("用户登出: {}", username);

        // 从缓存中移除用户信息
        loginUserCache.remove(username);

        // 清除SecurityContext
        SecurityContextHolder.clearContext();

        log.info("用户登出成功: {}", username);
    }

    /**
     * 获取登录用户信息
     *
     * @param username 用户名
     * @return 登录用户信息
     */
    public LoginUser getLoginUser(String username) {
        return loginUserCache.get(username);
    }

    /**
     * 刷新Token
     *
     * @param username 用户名
     * @return 新Token
     */
    public String refreshToken(String username) {
        LoginUser loginUser = loginUserCache.get(username);
        if (loginUser == null) {
            throw new ServiceException("用户未登录");
        }

        String newToken = jwtUtil.generateToken(loginUser);
        loginUser.setToken(newToken);
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + jwtProperties.getExpiration());

        loginUserCache.put(username, loginUser);

        return newToken;
    }
}
