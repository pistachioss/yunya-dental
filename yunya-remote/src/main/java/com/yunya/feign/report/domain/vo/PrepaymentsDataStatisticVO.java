package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 门诊预付款统计VO
 *
 * @author: chow
 * @date: 2020/12/8 15:32
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊预付款统计VO")
@Data
@ToString
public class PrepaymentsDataStatisticVO implements Serializable {
  /** 预付款充值总额（本金+赠金） */
  @ApiModelProperty("预付款充值总额（本金+赠金）")
  private BigDecimal totalPrepaymentsRechargeAmount;
  /** 预付款消费总额（本金+赠金） */
  @ApiModelProperty("预付款消费总额（本金+赠金）")
  private BigDecimal totalPrepaymentsExpendAmount;
  /** 预付款退费总额（本金+赠金） */
  @ApiModelProperty("预付款退费总额（本金+赠金）")
  private BigDecimal totalPrepaymentsExpendRefundAmount;
}
