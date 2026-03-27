package com.graduation.inventory.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置属性
 * 
 * @author graduation
 * @version 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT密钥
     */
    private String secret = "inventory-management-system-jwt-secret-key-2024";

    /**
     * 过期时间（单位：毫秒）
     * 默认86400000毫秒，即24小时
     */
    private Long expiration = 86400000L;

    /**
     * 请求头名称
     * 默认Authorization
     */
    private String header = "Authorization";

    /**
     * Token前缀
     * 默认Bearer 
     */
    private String prefix = "Bearer ";

    /**
     * Token头部（兼容配置）
     */
    private String tokenHead = "Bearer";

}
