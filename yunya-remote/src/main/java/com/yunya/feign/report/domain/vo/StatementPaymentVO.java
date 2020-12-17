package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
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
public class StatementPaymentVO implements Serializable {
  /** 支付方式ID */
  @ApiModelProperty("支付方式ID")
  private Integer accountItemId;
  /** 支付方式名称 */
  @ApiModelProperty("支付方式名称")
  private String accountItemName;
  /** 入账金额 */
  @ApiModelProperty("totalAmount")
  private BigDecimal totalAmount;
}
