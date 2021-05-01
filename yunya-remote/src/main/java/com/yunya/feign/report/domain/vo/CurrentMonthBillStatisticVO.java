package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 当月账单统计信息VO
 *
 * @author: chow
 * @date: 2021/1/5 16:04
 * @description:
 * @since: 1.0.0
 */
@ApiModel("当月账单统计信息VO")
@Data
@ToString
public class CurrentMonthBillStatisticVO implements Serializable {
  /** 当前月 */
  @ApiModelProperty("当前月")
  private String currentMonth;
  /** 当前门诊ID */
  @ApiModelProperty("当前门诊ID")
  private Integer orgId;
  /** 本月实收金额合计 */
  @ApiModelProperty("本月实收金额合计")
  private BigDecimal currentMonthTotalActualAmount;
  /** 本月优惠金额合计 */
  @ApiModelProperty("本月优惠金额合计")
  private BigDecimal currentMonthTotalDiscountAmount;
  /** 本月账单已收金额合计 */
  @ApiModelProperty("本月账单已收金额合计")
  private BigDecimal currentMonthTotalReceivedAmount;
  /** 本月账单欠费金额合计 */
  @ApiModelProperty("本月账单欠费金额合计")
  private BigDecimal currentMonthTotalDebtAmount;
}
