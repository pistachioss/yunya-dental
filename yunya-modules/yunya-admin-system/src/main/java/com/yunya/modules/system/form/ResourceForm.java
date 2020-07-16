package com.yunya.modules.system.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍: 权限参数封装模型
 *
 * @author: chow
 * @date: 2020/6/30 16:29
 * @description:
 * @since: 1.0.0
 */
@ApiModel("权限参数封装模型")
@Data
@ToString
public class ResourceForm implements Serializable {
  /** 功能权限ID */
  @ApiModelProperty(value = "菜单（功能权限）ID", required = true)
  private String resourceId;
  /** 功能权限类型 */
  @ApiModelProperty(value = "功能权限类型0-菜单权限；1-功能权限", required = true)
  private Byte resourceType;
}
