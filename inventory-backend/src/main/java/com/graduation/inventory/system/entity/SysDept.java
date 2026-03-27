package com.graduation.inventory.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.graduation.inventory.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 部门实体类
 * 
 * @author graduation
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class SysDept extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 父部门ID
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 部门名称
     */
    @TableField("dept_name")
    private String deptName;

    /**
     * 显示顺序
     */
    @TableField("order_num")
    private Integer orderNum;

    /**
     * 负责人
     */
    @TableField("leader")
    private String leader;

    /**
     * 联系电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

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

    /**
     * 子部门（非数据库字段）
     */
    @TableField(exist = false)
    private List<SysDept> children;
}
