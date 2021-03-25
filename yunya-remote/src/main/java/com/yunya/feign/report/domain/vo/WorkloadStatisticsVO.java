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
  /** 门诊实际工作量合计(已收工作量合计 - 退费工作量合计) */
  @ApiModelProperty("门诊实际工作量合计")
  private BigDecimal totalClinicActualWorkload;
  /** 工作量合计 */
  @ApiModelProperty("门诊已收工作量合计")
  private BigDecimal totalReceivedWorkload;
  /** 账单退费合计 */
  @ApiModelProperty("门诊账单退费工作量合计")
  private BigDecimal totalBillRefundWorkload;

  /** 门诊已收工作量合计 */
  @ApiModelProperty("已收工作量合计（首次收费）")
  private BigDecimal totalClinicReceivedWorkload;
  /** 其中含免单支付工作量合计 */
  @ApiModelProperty("其中含免单支付工作量合计（首次收费）")
  private BigDecimal totalFreePaymentWorkload;
  /** 门诊补入工作量合计 */
  @ApiModelProperty("补入工作量合计（首次收费）")
  private BigDecimal totalClinicCouponWorkload;
  /** (被代收)门诊已收工作量合计 */
  @ApiModelProperty("(被代收)已收工作量合计")
  private BigDecimal totalClinicBeCollectedReceivedWorkload;
  /** (被代收)其中含免单支付工作量合计 */
  @ApiModelProperty("(被代收)其中含免单支付工作量合计")
  private BigDecimal totalClinicBeCollectedFreePaymentWorkload;
  /** (被代收)门诊补入工作量合计 */
  @ApiModelProperty("(被代收)补入工作量合计")
  private BigDecimal totalClinicBeCollectedCouponWorkload;
  /** （收欠费）门诊已收工作量合计 */
  @ApiModelProperty("（收欠费）已收工作量合计")
  private BigDecimal totalClinicArrearsReceivedWorkload;
  /** （收欠费）门诊补入工作量合计 */
  @ApiModelProperty("（收欠费）补入工作量合计")
  private BigDecimal totalClinicArrearsCouponWorkload;
  /** （收欠费）其中含免单支付工作量合计 */
  @ApiModelProperty("（收欠费）其中含免单支付工作量合计")
  private BigDecimal totalClinicArrearsFreePaymentWorkload;

  /** 门诊已收非工作量合计（首次收费） */
  @ApiModelProperty("门诊已收非工作量合计（首次收费）")
  private BigDecimal totalClinicReceivedNotWorkload;
  /** 门诊已收非工作量合计（收欠费） */
  @ApiModelProperty("门诊已收非工作量合计（收欠费）")
  private BigDecimal totalClinicArrearsReceivedNotWorkload;
  /** 门诊非工作量合计（被代收） */
  @ApiModelProperty("门诊非工作量合计（被代收）")
  private BigDecimal totalBeCollectedNotWorkload;
}
