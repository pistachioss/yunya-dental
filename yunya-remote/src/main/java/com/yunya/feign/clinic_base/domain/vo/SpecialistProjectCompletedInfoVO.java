package com.yunya.feign.clinic_base.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简介: 专科完成信息VO
 *
 * @author: chow
 * @date: 2020/12/28 15:37
 * @description:
 * @since: 1.0.0
 */
@ApiModel("专科完成信息VO")
@Data
@ToString
public class SpecialistProjectCompletedInfoVO implements Serializable {
  /** 专科项目完成数量 */
  @ApiModelProperty("专科项目完成数量")
  private Integer specialistProjectCompleted;
  /** 完成专科项目明细 */
  @ApiModelProperty("完成专科项目明细")
  private List<SpecialistProjectCompletedDetailVO> specialistProjectCompletedDetails;
}
