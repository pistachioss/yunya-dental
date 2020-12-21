package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 对账单账单收入统计VO
 *
 * @author: chow
 * @date: 2020/12/18 10:55
 * @description:
 * @since: 1.0.0
 */
@ApiModel("对账单账单收入统计VO")
@Data
@ToString
public class StatementBillIncomeStatisticVO implements Serializable {
  /** 本月实收合计 */
  @ApiModelProperty("本月实收合计")
  private BigDecimal totalActualAmountThisMonth;
  /** 本月优惠金额合计 */
  @ApiModelProperty("本月优惠金额合计")
  private BigDecimal totalPrivilegeAmountThisMonth;
  /** 本月账单已收金额合计 */
  @ApiModelProperty("本月账单已收金额合计")
  private BigDecimal totalReceivedAmountThisMonth;
  /** 本月账单欠费合计 */
  @ApiModelProperty("本月账单欠费金额合计")
  private BigDecimal totalDebtAmountThisMonth;
  /** 诊所代收(当月账单) */
  @ApiModelProperty("诊所代收(当月账单)")
  private BigDecimal totalClinicCollectionThisMonth;
  /** 诊所代收(非当月账单) */
  @ApiModelProperty("诊所代收(非当月账单)")
  private BigDecimal totalClinicCollectionOtherMonth;
}
