package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 患者消费信息VO
 *
 * @author: chow
 * @date: 2021/1/20 13:15
 * @description:
 * @since: 1.0.0
 */
@ApiModel("患者消费信息VO")
@Data
@ToString
public class PatientCostInfoVO implements Serializable {
  /** 累计消费 */
  @ApiModelProperty("累计消费")
  private BigDecimal cumulativeConsumption;
  /** 欠费总额 */
  @ApiModelProperty("欠费总额")
  private BigDecimal totalArrears;
}
