package com.yunya.modules.system.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 简介: 入账方式新增参数模型
 *
 * @author: chow
 * @date: 2020/7/27 10:06
 * @description:
 * @since: 1.0.0
 */
@ApiModel("入账方式新增参数模型")
@Data
@ToString
public class AccountItemModel implements Serializable {
  /** 入账方式分类ID */
  @ApiModelProperty(value = "入账方式分类ID", required = true)
  @NotNull(message = "入账方式分类ID不能为空！")
  private Integer accountTypeId;
  /** 入账方式名称 */
  @ApiModelProperty(value = "入账方式名称", required = true)
  @NotBlank(message = "入账方式名称")
  @Size(max = 50, message = "入账方式名称长度不能超过50个字符")
  private String name;
  /** 入账方式类型 */
  @ApiModelProperty(value = "入账方式类型（0-现金类；1-预收类；2-优惠类；3-平台结算类）", required = true)
  @NotNull(message = "入账方式类型不能为空！")
  private Byte type;
}
