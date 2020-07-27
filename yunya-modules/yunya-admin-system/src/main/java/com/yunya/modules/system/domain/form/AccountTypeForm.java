package com.yunya.modules.system.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 简介: 入账方式分类修改参数模型
 *
 * @author: chow
 * @date: 2020/7/24 14:55
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("入账方式分类修改参数模型")
public class AccountTypeForm implements Serializable {
  /** 入账方式 */
  @ApiModelProperty(value = "入账方式名称", required = true)
  @NotBlank(message = "入账方式名称不能为空！")
  private String name;
  /** 是否启用 */
  @ApiModelProperty(value = "是否启用")
  private Boolean inservice;
}
