package com.yunya.feign.clinic_base.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 月业务目标参数模型
 *
 * @author: chow
 * @date: 2020/12/22 17:14
 * @description:
 * @since: 1.0.0
 */
@ApiModel("月业务目标参数模型")
@Data
@ToString
public class BusinessTargetOfMonthModel implements Serializable {
  /** 月份 */
  @ApiModelProperty(value = "月份", required = true)
  @Min(value = 1, message = "月份最小值为1")
  @Max(value = 12, message = "月份最大值为12")
  private Byte monthNum;
  /** 月业务目标数 */
  @ApiModelProperty(value = "月业务目标数", required = true, example = "1")
  @NotNull(message = "月业务目标不能为空！")
  @Min(value = 1, message = "月业务目标不能填0或负数！")
  private BigDecimal businessGoal;
}
