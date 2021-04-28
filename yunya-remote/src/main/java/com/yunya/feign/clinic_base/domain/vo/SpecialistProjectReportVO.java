package com.yunya.feign.clinic_base.domain.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 就诊患者分析-专科项目数据返回模板
 *
 * @author: WY
 * @date: 2020/12/30 16:33
 * @description:
 * @since: 1.0.0
 */
@ApiModel("就诊患者分析-专科项目数据返回模板")
@Data
@ToString
public class SpecialistProjectReportVO implements Serializable {

  /** 专科项目 */
  private String specialistProjectName;

  /** 占比 */
  private String percentage;
}
