package com.graduation.inventory.common.enums;

/**
 * 业务操作类型枚举
 *
 * @author graduation
 * @version 1.0.0
 */
public enum BusinessType {

    /**
     * 新增
     */
    INSERT("新增"),

    /**
     * 修改
     */
    UPDATE("修改"),

    /**
     * 删除
     */
    DELETE("删除"),

    /**
     * 授权
     */
    GRANT("授权"),

    /**
     * 导出
     */
    EXPORT("导出"),

    /**
     * 导入
     */
    IMPORT("导入"),

    /**
     * 强退
     */
    FORCE("强退"),

    /**
     * 清空
     */
    CLEAN("清空"),

    /**
     * 其他
     */
    OTHER("其他");

    private final String description;

    BusinessType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
