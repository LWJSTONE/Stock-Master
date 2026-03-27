package com.graduation.inventory.system.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 路由VO
 * 
 * @author graduation
 * @version 1.0.0
 */
@Data
public class RouterVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 路由名称
     */
    private String name;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 重定向地址
     */
    private String redirect;

    /**
     * 路由元信息
     */
    private RouterMeta meta;

    /**
     * 子路由
     */
    private List<RouterVo> children;
}
