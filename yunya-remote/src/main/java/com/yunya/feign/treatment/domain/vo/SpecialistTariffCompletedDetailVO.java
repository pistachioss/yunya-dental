package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 专科项目完成详情VO
 *
 * @author: chow
 * @date: 2020/12/28 15:48
 * @description:
 * @since: 1.0.0
 */
@ApiModel("专科项目完成详情VO")
@Data
@ToString
public class SpecialistTariffCompletedDetailVO implements Serializable {
  /** 项目明细ID */
  @ApiModelProperty("项目明细ID")
  private Integer tariffItemId;
  /** 项目明细名称 */
  @ApiModelProperty("项目明细名称")
  private String tariffItemName;
  /** 完成数量 */
  @ApiModelProperty("完成数量")
  private Integer completedAmount;
}
