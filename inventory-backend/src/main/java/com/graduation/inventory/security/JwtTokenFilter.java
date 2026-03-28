package com.graduation.inventory.security;

import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;

/**
 * JWT Token过滤器
 * 继承OncePerRequestFilter，确保每个请求只过滤一次
 * 
 * @author graduation
 * @version 1.0.0
 */
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenFilter.class);

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 从请求头获取Token
            String token = getTokenFromRequest(request);
            
            if (StrUtil.isNotBlank(token)) {
                // 解析Token获取用户名
                String username = getUsernameFromToken(token);
                
                // 验证Token有效性
                if (StrUtil.isNotBlank(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 加载用户详情
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    
                    // 验证Token是否有效
                    if (validateToken(token, userDetails)) {
                        // 创建认证对象
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        // 设置SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        log.debug("用户 {} 认证成功", username);
                    }
                }
            }
        } catch (Exception e) {
            log.error("JWT Token验证失败: {}", e.getMessage());
            // 清除SecurityContext
            SecurityContextHolder.clearContext();
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头获取Token
     * 
     * @param request HTTP请求
     * @return Token字符串
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader(jwtProperties.getHeader());
        
        if (StrUtil.isNotBlank(header) && header.startsWith(jwtProperties.getPrefix())) {
            return header.substring(jwtProperties.getPrefix().length());
        }
        
        return null;
    }

    /**
     * 从Token中获取用户名
     * 
     * @param token JWT Token
     * @return 用户名
     */
    private String getUsernameFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            log.error("从Token中获取用户名失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从Token中获取Claims
     * 
     * @param token JWT Token
     * @return Claims
     */
    private Claims getClaimsFromToken(String token) {
        Key key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证Token是否有效
     * 
     * @param token       JWT Token
     * @param userDetails 用户详情
     * @return 是否有效
     */
    private boolean validateToken(String token, UserDetails userDetails) {
        try {
            Claims claims = getClaimsFromToken(token);
            String username = claims.getSubject();
            
            // 验证用户名是否匹配
            if (!username.equals(userDetails.getUsername())) {
                log.warn("Token用户名不匹配: {} != {}", username, userDetails.getUsername());
                return false;
            }
            
            // 验证Token是否过期
            if (claims.getExpiration() != null && claims.getExpiration().getTime() < System.currentTimeMillis()) {
                log.warn("Token已过期: {}", claims.getExpiration());
                return false;
            }
            
            return true;
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            return false;
        }
    }

}
