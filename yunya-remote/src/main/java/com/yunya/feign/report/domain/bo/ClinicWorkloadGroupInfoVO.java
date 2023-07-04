package com.yunya.feign.report.domain.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 门诊工作量组合信息VO
 *
 * @author: chow
 * @date: 2021/3/21 14:33
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊工作量组合信息VO")
@Data
@ToString
public class ClinicWorkloadGroupInfoVO implements Serializable {

  public ClinicWorkloadGroupInfoVO(){}

  public ClinicWorkloadGroupInfoVO(boolean init) {
    init();
  }

  public void init() {
    firstReceivedWorkload = BigDecimal.ZERO;
    firstSwipeWorkload = BigDecimal.ZERO;
    firstCouponWorkload = BigDecimal.ZERO;
    firstFreePayWorkload = BigDecimal.ZERO;
    arrearsReceivedWorkload = BigDecimal.ZERO;
    arrearsCouponWorkload = BigDecimal.ZERO;
    arrearsFreePayWorkload = BigDecimal.ZERO;
    beCollectedReceivedWorkload = BigDecimal.ZERO;
    beCollectedCouponWorkload = BigDecimal.ZERO;
    beCollectedFreePayWorkload = BigDecimal.ZERO;

    firstReceivedNotWorkload = BigDecimal.ZERO;
    arrearsNotWorkload = BigDecimal.ZERO;
    beCollectedNotWorkload = BigDecimal.ZERO;
  }

  /** 首次收费总额 */
  @ApiModelProperty("首次收费总额")
  private BigDecimal firstReceivedAmount;
  /** 首次收费工作量合计 */
  @ApiModelProperty("首次收费工作量合计")
  private BigDecimal firstReceivedWorkload;

  @ApiModelProperty("首次划扣卡核销工作量合计")
  private BigDecimal firstSwipeWorkload;

  /** 首次收费免单工作量合计 */
  @ApiModelProperty("首次收费免单工作量合计")
  private BigDecimal firstFreePayWorkload;
  /** 首次收费补入工作量合计 */
  @ApiModelProperty("首次收费补入工作量合计")
  private BigDecimal firstCouponWorkload;
  /** 被代收总额 */
  @ApiModelProperty("被代收总额")
  private BigDecimal beCollectedReceivedAmount;
  /** 被代收工作量合计 */
  @ApiModelProperty("被代收工作量合计")
  private BigDecimal beCollectedReceivedWorkload;
  /** 被代收免单工作量合计 */
  @ApiModelProperty("被代收免单工作量合计")
  private BigDecimal beCollectedFreePayWorkload;
  /** 被代收补入工作量合计 */
  @ApiModelProperty("被代收补入工作量合计")
  private BigDecimal beCollectedCouponWorkload;
  /** 收欠费总额 */
  @ApiModelProperty("收欠费总额")
  private BigDecimal arrearsReceivedAmount;
  /** 收欠费工作量合计 */
  @ApiModelProperty("收欠费工作量合计")
  private BigDecimal arrearsReceivedWorkload;
  /** 收欠费免单工作量合计 */
  @ApiModelProperty("收欠费免单工作量合计")
  private BigDecimal arrearsFreePayWorkload;
  /** 收欠费补入工作量合计 */
  @ApiModelProperty("收欠费补入工作量合计")
  private BigDecimal arrearsCouponWorkload;
  /** 退费工作量总和 */
  @ApiModelProperty("退费工作量总和")
  private BigDecimal totalRefundWorkload;
  /** 首次收费非工作量 */
  @ApiModelProperty("首次收费非工作量")
  private BigDecimal firstReceivedNotWorkload;
  /** 收欠费费工作量 */
  @ApiModelProperty("收欠费费工作量")
  private BigDecimal arrearsNotWorkload;
  /** 被代收非工作量 */
  @ApiModelProperty("被代收非工作量")
  private BigDecimal beCollectedNotWorkload;
}
