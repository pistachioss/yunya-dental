package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 门诊对账单出入账信息VO
 *
 * @author: chow
 * @date: 2020/12/11 13:45
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊对账单出入账信息VO")
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class StatementPaymentVO implements Serializable {
  /** 支付方式ID */
  @ApiModelProperty("支付方式ID")
  private Integer accountItemId;
  /** 支付方式名称 */
  @ApiModelProperty("支付方式名称")
  private String accountItemName;
  /** 入账金额 */
  @ApiModelProperty("入账金额（本金）")
  private BigDecimal totalAmount;
  /** 赠金 */
  @ApiModelProperty("赠金（支付方式为会员卡或预付款时有值）")
  private BigDecimal bonusAmount;
}
