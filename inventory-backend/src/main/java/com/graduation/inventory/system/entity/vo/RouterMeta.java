package com.graduation.inventory.system.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 路由元信息
 * 
 * @author graduation
 * @version 1.0.0
 */
@Data
public class RouterMeta implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单标题
     */
    private String title;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 是否缓存
     */
    private Boolean noCache;

    /**
     * 外链地址
     */
    private String link;

    public RouterMeta() {
        this.noCache = false;
    }

    public RouterMeta(String title, String icon) {
        this.title = title;
        this.icon = icon;
        this.noCache = false;
    }

    public RouterMeta(String title, String icon, Boolean noCache) {
        this.title = title;
        this.icon = icon;
        this.noCache = noCache;
    }
}
