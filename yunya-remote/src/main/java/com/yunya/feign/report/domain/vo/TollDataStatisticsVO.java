package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2020/12/8 15:04
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊收费数据统计VO")
@Data
@ToString
public class TollDataStatisticsVO implements Serializable {
  /** 收欠费合计 */
  @ApiModelProperty("收欠费合计")
  private BigDecimal totalReceivedDebtAmount;
  /** 已收金额合计 */
  @ApiModelProperty("已收金额合计")
  private BigDecimal totalReceivedAmount;
  /** 账单退费合计 */
  @ApiModelProperty("账单退费合计")
  private BigDecimal totalBillRefundAmount;
  /** 门诊代收金额合计 */
  @ApiModelProperty("门诊代收金额合计")
  private BigDecimal totalClinicCollectionAmount;
  /** 门诊被代收金额合计 */
  @ApiModelProperty("门诊被代收金额合计")
  private BigDecimal totalClinicCollectedAmount;
}
