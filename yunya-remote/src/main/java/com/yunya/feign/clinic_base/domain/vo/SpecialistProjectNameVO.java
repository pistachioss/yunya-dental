package com.yunya.feign.clinic_base.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 专科项目名称VO
 *
 * @author: chow
 * @date: 2020/12/22 15:58
 * @description:
 * @since: 1.0.0
 */
@ApiModel("专科项目名称VO")
@Data
@ToString
public class SpecialistProjectNameVO implements Serializable {
  /** 专科项目id */
  @ApiModelProperty("专科项目id")
  private Integer id;
  /** 专科项目名称 */
  @ApiModelProperty("专科项目名称")
  private String specialistProjectName;
}
