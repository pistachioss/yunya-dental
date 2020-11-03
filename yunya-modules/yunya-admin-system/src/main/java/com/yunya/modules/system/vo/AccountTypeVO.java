package com.yunya.modules.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 入账方式分类VO模型
 *
 * @author: chow
 * @date: 2020/7/24 19:21
 * @description:
 * @since: 1.0.0
 */
@ApiModel("入账方式分类VO模型")
@Data
@ToString
public class AccountTypeVO implements Serializable {
  @ApiModelProperty("入账方式id")
  private Integer id;
  /** 入账方式 */
  @ApiModelProperty("入账方式名称")
  private String name;
  /** 是否系统默认（系统默认无法修改/删除） */
  @ApiModelProperty("是否系统默认（系统默认无法修改/删除）0-非默认，1-系统默认")
  private Boolean sysDefault;
  /** 是否启用 */
  @ApiModelProperty("是否启用0-不启用，1-启用")
  private Boolean inservice;
}
