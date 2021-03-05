package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 门诊账单数据统计
 *
 * @author: chow
 * @date: 2020/12/8 15:03
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊账单数据统计")
@Data
@ToString
public class BillDataStatisticsVO implements Serializable {
  /** 原价合计 */
  @ApiModelProperty("原价合计")
  private BigDecimal totalOriginalAmount;
  /** 优惠金额合计 */
  @ApiModelProperty("优惠金额合计")
  private BigDecimal totalDiscountAmount;
  /** 实收金额合计 */
  @ApiModelProperty("实收金额合计")
  private BigDecimal totalActualReceiveAmount;
  /** 免单支付合计 */
  @ApiModelProperty("免单支付合计")
  private BigDecimal totalFreePaymentAmount;
  /** 欠费金额合计 */
  @ApiModelProperty("欠费金额合计")
  private BigDecimal totalDebtAmount;
}
