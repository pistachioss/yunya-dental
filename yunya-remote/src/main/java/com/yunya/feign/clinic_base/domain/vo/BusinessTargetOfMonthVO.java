package com.yunya.feign.clinic_base.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 月业务目标VO
 *
 * @author: chow
 * @date: 2020/12/23 10:20
 * @description:
 * @since: 1.0.0
 */
@ApiModel("月业务目标VO")
@Data
@ToString
public class BusinessTargetOfMonthVO implements Serializable {
  /** 月份 */
  @ApiModelProperty("月份")
  private String monthNum;
  /** 月业务目标数 */
  @ApiModelProperty("月业务目标数")
  private BigDecimal businessGoal;
}
