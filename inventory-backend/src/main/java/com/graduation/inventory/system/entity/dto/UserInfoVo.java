package com.graduation.inventory.system.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 用户信息响应VO
 *
 * @author graduation
 * @version 1.0.0
 */
@Data
@ApiModel("用户信息")
public class UserInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID")
    private Long userId;

    /**
     * 用户名
     */
    @ApiModelProperty("用户名")
    private String username;

    /**
     * 真实姓名
     */
    @ApiModelProperty("真实姓名")
    private String realName;

    /**
     * 头像
     */
    @ApiModelProperty("头像")
    private String avatar;

    /**
     * 部门ID
     */
    @ApiModelProperty("部门ID")
    private Long deptId;

    /**
     * 部门名称
     */
    @ApiModelProperty("部门名称")
    private String deptName;

    /**
     * 角色列表
     */
    @ApiModelProperty("角色列表")
    private Set<String> roles;

    /**
     * 权限列表
     */
    @ApiModelProperty("权限列表")
    private Set<String> permissions;
}
