package com.yunya.feign.report.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 初诊患者来源分布查询参数模型
 *
 * @author: chow
 * @date: 2021/1/15 14:12
 * @description:
 * @since: 1.0.0
 */
@ApiModel("初诊患者来源分布查询参数模型")
@Data
@ToString
public class PatientFirstTreatOriginQuery implements Serializable {
  /** 组织ID */
  @ApiModelProperty("组织ID")
  private Integer orgId;
  /** 查询开始时间 */
  @ApiModelProperty(value = "查询开始时间", example = "yyyy-MM")
  private String startDate;
  /** 查询结束时间 */
  @ApiModelProperty(value = "查询结束时间", example = "yyyy-MM")
  private String endDate;
}
