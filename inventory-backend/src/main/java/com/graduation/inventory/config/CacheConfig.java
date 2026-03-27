package com.graduation.inventory.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine缓存配置类
 *
 * @author graduation
 * @version 1.0.0
 */
@Configuration
public class CacheConfig {

    /**
     * 登录用户缓存名称
     */
    public static final String LOGIN_USER_CACHE = "loginUserCache";

    /**
     * 验证码缓存名称
     */
    public static final String CAPTCHA_CACHE = "captchaCache";

    /**
     * 配置登录用户缓存
     * 缓存登录用户信息，减少数据库查询
     *
     * @return 登录用户缓存
     */
    @Bean(LOGIN_USER_CACHE)
    public Cache<String, Object> loginUserCache() {
        return Caffeine.newBuilder()
                // 初始容量
                .initialCapacity(100)
                // 最大容量
                .maximumSize(10000)
                // 写入后过期时间（24小时）
                .expireAfterWrite(24, TimeUnit.HOURS)
                // 访问后过期时间（2小时无访问则过期）
                .expireAfterAccess(2, TimeUnit.HOURS)
                // 开启统计信息
                .recordStats()
                .build();
    }

    /**
     * 配置验证码缓存
     * 缓存图形验证码
     *
     * @return 验证码缓存
     */
    @Bean(CAPTCHA_CACHE)
    public Cache<String, Object> captchaCache() {
        return Caffeine.newBuilder()
                // 初始容量
                .initialCapacity(100)
                // 最大容量
                .maximumSize(10000)
                // 写入后过期时间（5分钟）
                .expireAfterWrite(5, TimeUnit.MINUTES)
                // 开启统计信息
                .recordStats()
                .build();
    }

    /**
     * 配置默认缓存
     * 用于一般数据缓存
     *
     * @return 默认缓存
     */
    @Bean
    public Cache<String, Object> defaultCache() {
        return Caffeine.newBuilder()
                // 初始容量
                .initialCapacity(100)
                // 最大容量
                .maximumSize(5000)
                // 写入后过期时间（1小时）
                .expireAfterWrite(1, TimeUnit.HOURS)
                // 开启统计信息
                .recordStats()
                .build();
    }
}
