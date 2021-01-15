package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 初诊患者来源VO
 *
 * @author: chow
 * @date: 2021/1/15 14:25
 * @description:
 * @since: 1.0.0
 */
@ApiModel("初诊患者来源VO")
@Data
@ToString
public class PatientFirstTreatOriginVO implements Serializable {
  /** 患者来源类型ID */
  @ApiModelProperty("患者来源类型ID")
  private Integer patientOriginTypeId;
  /** 患者来源类型名称 */
  @ApiModelProperty("患者来源类型名称")
  private String patientOriginTypeName;
  /** 初诊患者数量 */
  @ApiModelProperty("初诊患者数量")
  private Integer firstTreatCount;
  /** 初诊患者占比 */
  @ApiModelProperty("初诊患者占比")
  private BigDecimal firstTreatPercentage;
}
