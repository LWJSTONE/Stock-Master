package com.graduation.inventory.security;

import com.alibaba.fastjson2.JSON;
import com.graduation.inventory.common.constant.HttpStatus;
import com.graduation.inventory.common.domain.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 访问拒绝处理器
 * 处理已认证用户访问无权限资源的情况
 *
 * @author graduation
 * @version 1.0.0
 */
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(AccessDeniedHandlerImpl.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        
        log.error("访问权限不足: {}, URI: {}", accessDeniedException.getMessage(), request.getRequestURI());
        
        // 设置响应类型和编码
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        
        // 构建错误响应
        Result<Void> result = Result.error(HttpStatus.FORBIDDEN, "权限不足，无法访问该资源");
        
        // 输出JSON响应
        PrintWriter writer = response.getWriter();
        writer.write(JSON.toJSONString(result));
        writer.flush();
        writer.close();
    }
}
