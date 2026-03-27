package com.graduation.inventory.system.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 菜单树结构VO
 * 
 * @author graduation
 * @version 1.0.0
 */
@Data
public class MenuTreeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    private Long id;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 类型（M目录 C菜单 F按钮）
     */
    private String type;

    /**
     * 显示顺序
     */
    private Integer orderNum;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 子菜单
     */
    private List<MenuTreeVo> children;
}
