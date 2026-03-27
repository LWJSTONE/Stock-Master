package com.graduation.inventory.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.graduation.inventory.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单实体类
 * 
 * @author graduation
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 父菜单ID
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 菜单名称
     */
    @TableField("menu_name")
    private String menuName;

    /**
     * 路由地址
     */
    @TableField("path")
    private String path;

    /**
     * 组件路径
     */
    @TableField("component")
    private String component;

    /**
     * 权限标识
     */
    @TableField("perms")
    private String perms;

    /**
     * 类型（M目录 C菜单 F按钮）
     */
    @TableField("type")
    private String type;

    /**
     * 显示顺序
     */
    @TableField("order_num")
    private Integer orderNum;

    /**
     * 是否显示（0显示 1隐藏）
     */
    @TableField("visible")
    private String visible;

    /**
     * 状态（0正常 1停用）
     */
    @TableField("status")
    private String status;

    /**
     * 菜单图标
     */
    @TableField("icon")
    private String icon;
}
