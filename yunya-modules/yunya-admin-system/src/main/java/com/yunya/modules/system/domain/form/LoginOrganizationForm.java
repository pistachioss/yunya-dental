package com.yunya.modules.system.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> 用户可登录组织参数封装
 *
 * @author: chow
 * @date: 2020/6/15 13:42
 * @description:
 * @since: 1.0.0
 */
@ApiModel("用户可登录组织参数封装")
@Data
@ToString
public class LoginOrganizationForm implements Serializable {
  /** 组织ID */
  @ApiModelProperty("组织ID")
  private Integer orgId;
  /** 组织部门ID */
  @ApiModelProperty("组织部门ID")
  private Integer orgDeptId;
  /** 岗位ID */
  @ApiModelProperty("岗位ID")
  private Integer postId;
}
