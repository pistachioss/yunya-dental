package com.yunya.feign.clinic_base.domain.vo;

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
public class BusinessGoalVO implements Serializable {

  /** 数据所属ID */
  @ApiModelProperty("数据所属ID")
  private Integer belongId;

  /** 业务目标类型（0-实收金额；1-工作量；2-初诊人数；3-就诊人次） */
  @ApiModelProperty("业务目标类型（0-实收金额；1-工作量；2-初诊人数；3-就诊人次）")
  private Byte businessType;

  /** 业务目标数 */
  @ApiModelProperty("业务目标数")
  private BigDecimal businessGoal;
}
