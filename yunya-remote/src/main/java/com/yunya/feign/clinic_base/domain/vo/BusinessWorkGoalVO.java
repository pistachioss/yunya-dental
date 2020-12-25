package com.yunya.feign.clinic_base.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 业务目标VO
 *
 * @author: chow
 * @date: 2020/12/23 18:00
 * @description:
 * @since: 1.0.0
 */
@ApiModel("业务目标VO")
@Data
@ToString
public class BusinessWorkGoalVO implements Serializable {
  /** 日期 */
  @Excel(name = "日期")
  @ApiModelProperty("日期")
  private String businessDate;
  /** 目标实收金额（元） */
  @Excel(name = "目标实收金额（元）")
  @ApiModelProperty("目标实收金额（元）")
  private BigDecimal actualReceivedAmountGoal;
  /** 完成实收金额（元） */
  @Excel(name = "完成实收金额（元）")
  @ApiModelProperty("完成实收金额（元）")
  private BigDecimal actualReceivedAmountCompleted;
  /** 实收金额完成百分比 */
  @Excel(name = "实收金额完成百分比")
  @ApiModelProperty("实收金额完成百分比")
  private BigDecimal percentageOfCompletedAmount;
  /** 目标工作量（元） */
  @Excel(name = "目标工作量（元）")
  @ApiModelProperty("目标工作量（元）")
  private BigDecimal workloadAmountGoal;
  /** 完成工作量（元） */
  @Excel(name = "完成工作量（元）")
  @ApiModelProperty("完成工作量（元）")
  private BigDecimal workloadAmountCompleted;
  /** 工作量完成百分比 */
  @Excel(name = "工作量完成百分比")
  @ApiModelProperty("工作量完成百分比")
  private BigDecimal percentageOfWorkloadAmountCompleted;
  /** 目标初诊人数 */
  @Excel(name = "目标初诊人数")
  @ApiModelProperty("目标初诊人数")
  private Integer firstTreatPerNumGoal;
  /** 完成初诊人数 */
  @Excel(name = "完成初诊人数")
  @ApiModelProperty("完成初诊人数")
  private Integer firstTreatPerNumCompleted;
  /** 初诊人数完成百分比 */
  @Excel(name = "初诊人数完成百分比")
  @ApiModelProperty("初诊人数完成百分比")
  private BigDecimal percentageOfFirstTreatPerNumCompleted;
  /** 目标就诊人次 */
  @Excel(name = "目标就诊人次")
  @ApiModelProperty("目标就诊人次")
  private Integer treatPerTimesGoal;
  /** 完成就诊人次 */
  @Excel(name = "完成就诊人次")
  @ApiModelProperty("完成就诊人次")
  private Integer treatPerTimesCompleted;
  /** 就诊人次完成百分比 */
  @Excel(name = "就诊人次完成百分比")
  @ApiModelProperty("就诊人次完成百分比")
  private BigDecimal percentageOfTreatPerTimesCompleted;
}
