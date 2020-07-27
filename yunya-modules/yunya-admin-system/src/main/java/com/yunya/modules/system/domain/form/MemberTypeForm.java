package com.yunya.modules.system.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 会员卡信息修改参数模型
 *
 * @author: chow
 * @date: 2020/7/22 17:32
 * @description:
 * @since: 1.0.0
 */
@ApiModel("会员卡信息修改参数模型")
@Data
@ToString
public class MemberTypeForm implements Serializable {
  /** 会员卡名称 */
  @ApiModelProperty(value = "会员卡名称", required = true)
  @NotBlank(message = "会员卡名称不能为空！")
  @Size(max = 25, message = "会员卡名称长度不能超过25")
  private String name;
  /** 类型,0:普通,1:VIP */
  @ApiModelProperty(value = "会员类型", required = true)
  @NotNull(message = "会员类型不能为空！")
  private Byte type;
  /** 续费金额 */
  @ApiModelProperty(value = "会员卡名续费金额", required = true)
  @NotNull(message = "会员卡名续费金额不能为空！")
  @Min(value = 0, message = "会员卡名续费金额不能小于0")
  private BigDecimal renewalAmount;
  /** 年限 */
  @ApiModelProperty(value = "会员卡名续费有效年限", required = true)
  @NotNull(message = "会员卡名续费有效年限不能为空!")
  @Min(value = 1, message = "会员卡名续费有效年限不能小于0")
  private Integer ageLimit;
  /** 折扣率（价目表自动调价的折扣率） */
  @ApiModelProperty("会员卡折扣率")
  @NotNull(message = "会员卡折扣率不能为空！")
  @Min(value = 0, message = "会员卡折扣率不能小于0")
  @Max(value = 100, message = "会员卡折扣率不能大于100%")
  private Float rate;
  /** 图标 */
  @ApiModelProperty("会员卡图标")
  @NotNull(message = "会员卡图标不能为空！")
  private Byte icon;
  /** 会员卡描述（青藤、银藤、金藤、艾维会员） */
  @ApiModelProperty("描述")
  private String description;
}
