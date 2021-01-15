package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简介: 初诊患者来源分布信息VO
 *
 * @author: chow
 * @date: 2021/1/15 14:11
 * @description:
 * @since: 1.0.0
 */
@ApiModel("初诊患者来源分布信息VO")
@Data
@ToString
public class PatientFirstTreatOriginInfoVO implements Serializable {
  /** 初诊患者总数 */
  @ApiModelProperty("初诊患者总数")
  private Integer firstTreatTotalCount;
  /** 初诊患者来源列表 */
  private List<PatientFirstTreatOriginVO> patientFirstTreatOrigins;
}
