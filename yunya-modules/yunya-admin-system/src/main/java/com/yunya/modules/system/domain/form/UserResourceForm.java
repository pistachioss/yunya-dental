package com.yunya.modules.system.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍: 用户可登陆组织信息
 *
 * @author: chow
 * @date: 2020/7/1 11:32
 * @description:
 * @since: 1.0.0
 */
@ApiModel("用户可登陆组织信息")
@Data
@ToString
public class UserResourceForm implements Serializable {
  /** 用户ID */
  @ApiModelProperty("用户ID")
  private Integer userId;
  /** 用户登陆组织ID */
  @ApiModelProperty("组织ID")
  private Integer orgId;
}
