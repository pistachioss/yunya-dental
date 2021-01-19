package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 运营综合数据VO
 *
 * @author: chow
 * @date: 2021/1/18 16:21
 * @description:
 * @since: 1.0.0
 */
@ApiModel("运营综合数据VO（工作量完成率、初诊人数目标完成率、老患者介绍率、随访完成率、提醒完成率）")
@Data
@ToString
public class OperationDataComplexInfoVO implements Serializable {
  /** 数据名称 */
  @ApiModelProperty("数据名称")
  private String complexInfoName;
  /** 目标数 */
  @ApiModelProperty("目标数")
  private String goalCount;
  /** 完成数 */
  @ApiModelProperty("完成数")
  private String completedCount;
  /** 完成百分比 */
  @ApiModelProperty("完成百分比")
  private BigDecimal completedPercentage;
}
