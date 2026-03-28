package com.graduation.inventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS跨域配置类
 *
 * @author graduation
 * @version 1.0.0
 */
@Configuration
public class CorsConfig {

    /**
     * 配置CORS过滤器
     *
     * @return CORS过滤器
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许所有来源
        // 在生产环境中建议配置具体的域名
        config.addAllowedOriginPattern("*");
        
        // 允许所有方法 (GET, POST, PUT, DELETE, OPTIONS等)
        config.addAllowedMethod("*");
        
        // 允许所有头部
        config.addAllowedHeader("*");
        
        // 允许携带凭证（如Cookie）
        config.setAllowCredentials(true);
        
        // 预检请求缓存时间（秒）
        config.setMaxAge(3600L);
        
        // 暴露的响应头
        config.addExposedHeader("Authorization");
        config.addExposedHeader("Content-Disposition");
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有路径应用CORS配置
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
