package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 完成业务目标VO
 *
 * @author: chow
 * @date: 2020/12/24 20:46
 * @description:
 * @since: 1.0.0
 */
@ApiModel("完成业务目标VO")
@Data
@ToString
public class BusinessCompletedWorkGoalVO implements Serializable {
  /** 完成实收金额（元） */
  @ApiModelProperty("完成实收金额（元）")
  private BigDecimal actualReceivedAmountCompleted;
  /** 完成工作量（元） */
  @ApiModelProperty("完成工作量（元）")
  private BigDecimal workloadAmountCompleted;
  /** 完成初诊人数 */
  @ApiModelProperty("完成初诊人数")
  private Integer firstTreatPerNumCompleted;
  /** 完成就诊人次 */
  @ApiModelProperty("完成就诊人次")
  private Integer treatPerTimesCompleted;
}
