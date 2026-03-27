package com.graduation.inventory.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.graduation.inventory.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体类
 * 
 * @author graduation
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 角色名称
     */
    @TableField("role_name")
    private String roleName;

    /**
     * 角色权限字符串
     */
    @TableField("role_key")
    private String roleKey;

    /**
     * 数据范围（1全部 2自定义 3本部门 4本部门及以下 5仅本人）
     */
    @TableField("data_scope")
    private String dataScope;

    /**
     * 状态（0正常 1停用）
     */
    @TableField("status")
    private String status;

    /**
     * 删除标志（0存在 1删除）
     */
    @TableField("del_flag")
    private String delFlag;
}
