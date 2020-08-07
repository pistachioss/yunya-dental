package com.yunya.modules.system.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * 简介: 用户可登陆组织新增参数模型
 *
 * @author: chow
 * @date: 2020/8/3 13:10
 * @description:
 * @since: 1.0.0
 */
@ApiModel("用户可登陆组织新增参数模型")
@Data
@ToString
public class SysUserPostModel {
  @ApiModelProperty(value = "用户ID", required = true)
  @NotNull(message = "用户ID不能为空")
  private Integer userId;

  @ApiModelProperty(value = "可登录组织ID", required = true)
  @NotNull(message = "组织ID不能为空")
  private Integer companyId;

  /** 部门ID */
  @ApiModelProperty(value = "组织部门ID", required = true)
  @NotNull(message = "组织部门ID不能为空")
  private Integer departmentId;

  /** 岗位ID */
  @ApiModelProperty(value = "岗位ID", required = true)
  @NotNull(message = "岗位ID不能为空")
  private Integer postId;

  /** 角色组ID */
  @ApiModelProperty(value = "岗位组ID", required = true)
  private Integer postGroupId;

  /** 系统终端ID */
  @ApiModelProperty("系统终端ID")
  private Integer systemId;
}
