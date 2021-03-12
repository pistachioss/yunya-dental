package com.yunya.feign.clinic_base.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

/**
 * 简介: 专科项目VO
 *
 * @author: chow
 * @date: 2020/12/22 14:44
 * @description:
 * @since: 1.0.0
 */
@ApiModel("专科项目VO")
@Data
@ToString
public class SpecialistProjectTargetVO implements Serializable {
  /** 专科项目ID */
  @ApiModelProperty("专科项目ID")
  private Integer id;
  /** 专科项目名称 */
  @ApiModelProperty("专科项目名称")
  private String name;
  /** 项目ID列表 */
  @ApiModelProperty("项目ID列表")
  private String tariffIds;
  /** 专科项目业务目标*/
  @ApiModelProperty("专科项目业务目标")
  Map<Integer, Integer> targetMap;
}
