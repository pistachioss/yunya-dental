package com.yunya.feign.treatment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 简介: 会员卡账户参数模型
 *
 * @author: chow
 * @date: 2020/8/26 17:39
 * @description:
 * @since: 1.0.0
 */
@ApiModel("会员卡账户参数模型")
@Data
@ToString
public class MemberAccountModel {

  /** 会员卡账户ID */
  @ApiModelProperty(value = "会员卡卡号", required = true)
  @NotBlank(message = "会员卡卡号不能为空！")
  private String memberNum;

  @ApiModelProperty(value = "入账方式ID", required = true)
  @NotNull(message = "入账方式ID不能为空！")
  private Integer accountItemId;

  /** 支付金额 */
  @ApiModelProperty(value = "支付金额", required = true)
  @NotNull(message = "支付金额不能为空！")
  @Min(value = 0, message = "输入金额不能小于0！")
  private BigDecimal amount;
}
