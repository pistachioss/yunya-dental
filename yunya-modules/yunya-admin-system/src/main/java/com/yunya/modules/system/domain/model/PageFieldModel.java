package com.yunya.modules.system.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 简介: 页面字段参数模型
 *
 * @author: chow
 * @date: 2020/9/1 10:19
 * @description:
 * @since: 1.0.0
 */
@ApiModel("页面字段参数模型")
@Data
@ToString
public class PageFieldModel implements Serializable {
  /** 字段名称 */
  @ApiModelProperty(value = "字段名称", required = true)
  @NotBlank(message = "字段名称")
  private String fieldName;

  @ApiModelProperty("是否默认字段（默认字段无法隐藏，移动；默认0-否）")
  private Boolean defaultValue = false;

  @ApiModelProperty("字段是否隐藏（默认显示（0-显示；1-隐藏））")
  private Boolean hide = false;
}
