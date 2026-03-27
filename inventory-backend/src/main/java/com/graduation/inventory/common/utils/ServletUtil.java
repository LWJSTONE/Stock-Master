package com.graduation.inventory.common.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet工具类
 *
 * @author graduation
 * @version 1.0.0
 */
public class ServletUtil {

    private ServletUtil() {
    }

    /**
     * 获取当前请求
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * 获取当前响应
     *
     * @return HttpServletResponse
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getResponse() : null;
    }

    /**
     * 获取当前会话
     *
     * @return HttpSession
     */
    public static HttpSession getSession() {
        HttpServletRequest request = getRequest();
        return request != null ? request.getSession() : null;
    }

    /**
     * 获取请求参数
     *
     * @param name 参数名
     * @return 参数值
     */
    public static String getParameter(String name) {
        HttpServletRequest request = getRequest();
        return request != null ? request.getParameter(name) : null;
    }

    /**
     * 获取客户端IP地址
     *
     * @return IP地址
     */
    public static String getClientIP() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return "unknown";
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (isUnknown(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isUnknown(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isUnknown(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (isUnknown(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (isUnknown(ip)) {
            ip = request.getRemoteAddr();
        }

        // 对于多次代理的情况，取第一个非unknown的IP
        if (ip != null && ip.length() > 15 && ip.indexOf(",") > 0) {
            ip = ip.substring(0, ip.indexOf(","));
        }

        return ip;
    }

    /**
     * 检查IP是否为unknown
     *
     * @param ip IP地址
     * @return 是否为unknown
     */
    private static boolean isUnknown(String ip) {
        return ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip);
    }

    /**
     * 获取请求URI
     *
     * @return URI
     */
    public static String getRequestURI() {
        HttpServletRequest request = getRequest();
        return request != null ? request.getRequestURI() : null;
    }

    /**
     * 获取请求URL
     *
     * @return URL
     */
    public static String getRequestURL() {
        HttpServletRequest request = getRequest();
        return request != null ? request.getRequestURL().toString() : null;
    }

    /**
     * 获取请求方法
     *
     * @return 请求方法
     */
    public static String getMethod() {
        HttpServletRequest request = getRequest();
        return request != null ? request.getMethod() : null;
    }

    /**
     * 获取请求头
     *
     * @param name 头名称
     * @return 头值
     */
    public static String getHeader(String name) {
        HttpServletRequest request = getRequest();
        return request != null ? request.getHeader(name) : null;
    }
}
