package com.yunya.modules.system.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍: 系统页面功能列表查询参数封装
 *
 * @author: chow
 * @date: 2020/6/30 11:54
 * @description:
 * @since: 1.0.0
 */
@ApiModel("系统页面功能列表查询参数模型")
@Data
@ToString
public class MenuElementForm implements Serializable {
  /**系统类型*/
  @ApiModelProperty("系统类型 0-管理端；1-前台端")
  private Integer systemId;
  /** 菜单类型 */
  @ApiModelProperty("菜单类型；0-web;1-app")
  private Byte menuType;
  /** 菜单名称 */
  @ApiModelProperty("菜单名称")
  private String title;
  /** 岗位ID */
  @ApiModelProperty("岗位ID")
  private Integer postId;
}
