package com.yunya.feign.report.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 简介: 未复诊且未提醒患者删除参数模型
 *
 * @author: chow
 * @date: 2021/12/22 09:35
 * @description:
 * @since: 1.0.0
 */
@ApiModel("未复诊且未提醒患者删除参数模型")
@Data
public class PatientNotSeenForm implements Serializable {
  @ApiModelProperty(value = "患者ID", required = true)
  private Integer patientId;

  @ApiModelProperty(value = "开始末次就诊日期", required = true)
  private String startDate;

  @ApiModelProperty(value = "结束末次就诊日期", required = true)
  private String endDate;
}
