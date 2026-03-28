package com.graduation.inventory.security;

import com.alibaba.fastjson2.JSON;
import com.graduation.inventory.common.constant.HttpStatus;
import com.graduation.inventory.common.domain.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 未认证处理入口
 * 处理未认证用户访问受保护资源的情况
 *
 * @author graduation
 * @version 1.0.0
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationEntryPointImpl.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        
        log.error("认证失败: {}, URI: {}", authException.getMessage(), request.getRequestURI());
        
        // 设置响应类型和编码
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        // 构建错误响应
        Result<Void> result = Result.error(HttpStatus.UNAUTHORIZED, "认证失败，请重新登录");
        
        // 输出JSON响应
        PrintWriter writer = response.getWriter();
        writer.write(JSON.toJSONString(result));
        writer.flush();
        writer.close();
    }
}
