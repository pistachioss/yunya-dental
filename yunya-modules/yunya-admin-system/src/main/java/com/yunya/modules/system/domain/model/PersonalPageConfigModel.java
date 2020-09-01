package com.yunya.modules.system.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.LinkedHashSet;

/**
 * 简介: 个人页面字段显示信息新增参数模型
 *
 * @author: chow
 * @date: 2020/8/31 20:48
 * @description:
 * @since: 1.0.0
 */
@ApiModel("个人页面字段显示信息新增参数模型")
@Data
@ToString
public class PersonalPageConfigModel implements Serializable {

  /** 页面名称 */
  @ApiModelProperty(value = "页面名称", required = true)
  @NotBlank(message = "页面名称不能为空！")
  private String pageName;

  /** 页面字段 */
  private LinkedHashSet<PageFieldModel> fieldList;
}
