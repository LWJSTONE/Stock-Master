package com.graduation.inventory.security;

import com.alibaba.fastjson2.JSON;
import com.graduation.inventory.common.domain.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 登出成功处理器
 *
 * @author graduation
 * @version 1.0.0
 */
@Component
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(LogoutSuccessHandlerImpl.class);

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        
        log.info("用户登出成功, URI: {}", request.getRequestURI());
        
        // 设置响应类型和编码
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        
        // 构建成功响应
        Result<Void> result = Result.<Void>success("登出成功", null);
        
        // 输出JSON响应
        PrintWriter writer = response.getWriter();
        writer.write(JSON.toJSONString(result));
        writer.flush();
        writer.close();
    }
}
