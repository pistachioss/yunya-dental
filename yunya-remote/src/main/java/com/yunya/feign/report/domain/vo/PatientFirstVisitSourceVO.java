package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 初诊患者来源VO
 *
 * @author: chenl
 * @date: 2021/1/15 14:11
 * @description:
 * @since: 1.0.0
 */
@ApiModel("初诊患者来源VO")
@Data
@ToString
public class PatientFirstVisitSourceVO implements Serializable {
  /** 初诊患者总数 */
  @ApiModelProperty("初诊患者总数")
  private Integer firstVisitCount;
  /** 门诊ID */
  @ApiModelProperty("门诊ID")
  private Integer orgId;
  /** 患者来源ID */
  @ApiModelProperty("患者来源ID")
  private Integer originType;
  /** 患者来源名称 */
  @ApiModelProperty("患者来源名称")
  private String originTypeName;
}
