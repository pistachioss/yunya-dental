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
  /** 实际工作量合计(已收工作量合计 - 退费工作量合计) */
  @ApiModelProperty("实际工作量合计")
  private BigDecimal totalClinicActualWorkload;
  /** 工作量合计 */
  @ApiModelProperty("已收工作量合计")
  private BigDecimal totalReceivedWorkload;
  /** 账单退费合计 */
  @ApiModelProperty("账单退费工作量合计")
  private BigDecimal totalBillRefundWorkload;

  /** 门诊已收工作量合计 */
  @ApiModelProperty("门诊已收工作量合计（首次收费）")
  private BigDecimal totalClinicReceivedWorkload;
  /** 其中含免单支付工作量合计 */
  @ApiModelProperty("其中含免单支付工作量合计（首次收费）")
  private BigDecimal totalFreePaymentAmount;
  /** 门诊补入工作量合计 */
  @ApiModelProperty("门诊补入工作量合计（首次收费）")
  private BigDecimal totalClinicCouponWorkload;
  /** (被代收)门诊已收工作量合计 */
  @ApiModelProperty("(被代收)门诊已收工作量合计")
  private BigDecimal totalClinicedReceivedWorkload;

  /** 被代收其中含免单支付工作量合计 */
  @ApiModelProperty("被代收其中含免单支付工作量合计")
  private BigDecimal totalClinicedFreePaymentAmount;

  /** 被代收门诊补入工作量合计 */
  @ApiModelProperty("被代收门诊补入工作量合计")
  private BigDecimal totalClinicedCouponWorkload;

  /** 收欠费门诊已收工作量合计 */
  @ApiModelProperty("收欠费门诊已收工作量合计")
  private BigDecimal totalClinicArrearsReceivedWorkload;

  /** 收欠费门诊补入工作量合计 */
  @ApiModelProperty("收欠费门诊补入工作量合计")
  private BigDecimal totalClinicArrearsCouponWorkload;

  /** 收欠费其中含免单支付工作量合计 */
  @ApiModelProperty("收欠费其中含免单支付工作量合计")
  private BigDecimal totalClinicFreePaymentWorkload;

  /** 门诊已收非工作量合计 */
  @ApiModelProperty("门诊已收非工作量合计")
  private BigDecimal totalClinicReceivedNotWorkload;

  /** 收欠费门诊已收非工作量合计 */
  @ApiModelProperty("收欠费门诊已收非工作量合计")
  private BigDecimal totalClinicArrearsReceivedNotWorkload;

  /** 被代收门诊非工作量合计 */
  @ApiModelProperty("被代收门诊非工作量合计")
  private BigDecimal totalClinicedNotWorkload;
}
