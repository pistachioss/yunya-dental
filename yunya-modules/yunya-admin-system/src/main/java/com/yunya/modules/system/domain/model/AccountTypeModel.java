package com.yunya.modules.system.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 简介: 入账方式分类参数模型
 *
 * @author: chow
 * @date: 2020/7/24 14:11
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("入账方式分类新增模型")
public class AccountTypeModel implements Serializable {
  /** 入账方式名称 */
  @ApiModelProperty(value = "入账方式分类名称", required = true)
  @NotBlank(message = "入账方式分类名称不能为空！")
  @Size(max = 25, message = "名称长度不能超过25个字符")
  private String name;
}
