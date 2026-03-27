package com.graduation.inventory.system.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 部门树结构VO
 *
 * @author graduation
 * @version 1.0.0
 */
@Data
@ApiModel("部门树结构")
public class DeptTreeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    @ApiModelProperty("部门ID")
    private Long id;

    /**
     * 父部门ID
     */
    @ApiModelProperty("父部门ID")
    private Long parentId;

    /**
     * 部门名称
     */
    @ApiModelProperty("部门名称")
    private String deptName;

    /**
     * 显示顺序
     */
    @ApiModelProperty("显示顺序")
    private Integer orderNum;

    /**
     * 负责人
     */
    @ApiModelProperty("负责人")
    private String leader;

    /**
     * 联系电话
     */
    @ApiModelProperty("联系电话")
    private String phone;

    /**
     * 状态（0正常 1停用）
     */
    @ApiModelProperty("状态（0正常 1停用）")
    private String status;

    /**
     * 子部门
     */
    @ApiModelProperty("子部门")
    private List<DeptTreeVo> children;
}
