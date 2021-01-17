package com.yunya.feign.clinic_base.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 业务目标完成情况VO
 *
 * @author: chow
 * @date: 2021/1/14 20:23
 * @description:
 * @since: 1.0.0
 */
@ApiModel("业务目标完成情况VO")
@Data
@ToString
public class BusinessGoalCompletedInfoVO implements Serializable {
  /** 目标值 */
  @ApiModelProperty("目标值")
  private BigDecimal businessGoalCount;
  /** 完成值 */
  @ApiModelProperty("完成值")
  private BigDecimal businessCompletedCount;
  /** 完成百分比 */
  @ApiModelProperty("完成百分比")
  private BigDecimal businessCompletedPercentage;
}
