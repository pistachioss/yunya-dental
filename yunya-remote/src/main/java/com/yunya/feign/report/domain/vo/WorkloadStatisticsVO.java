package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 门诊工作量VO
 *
 * @author: chow
 * @date: 2020/12/8 15:21
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊工作量VO")
@Data
@ToString
public class WorkloadStatisticsVO implements Serializable {
  /** 门诊实收工作量合计 */
  @ApiModelProperty("门诊实收工作量合计")
  private BigDecimal totalClinicActualReceiveWorkload;
  /** 门诊已收工作量合计 */
  @ApiModelProperty("门诊已收工作量合计")
  private BigDecimal totalClinicReceivedWorkload;
  /** 门诊已收非工作量合计 */
  @ApiModelProperty("门诊已收非工作量合计")
  private BigDecimal totalClinicReceivedNotWorkload;
  /** 免单支付合计 */
  @ApiModelProperty("免单支付合计")
  private BigDecimal totalFreePaymentAmount;
  /** 门诊补入工作量合计 */
  @ApiModelProperty("门诊补入工作量合计")
  private BigDecimal totalClinicCouponWorkload;
}
