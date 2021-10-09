package com.yunya.feign.clinic_base.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

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
public class SpecialistProjectVO implements Serializable {
  /** 专科项目ID */
  @ApiModelProperty("专科项目ID")
  private Integer id;
  /** 专科项目名称 */
  @ApiModelProperty("专科项目名称")
  private String specialistProjectName;
  /** 对应价目表项目Id */
  @ApiModelProperty("对应价目表项目Id")
  private String tariffItemIds;
  /** 对应价目表项目 */
  @ApiModelProperty("对应价目表项目名称")
  private String tariffItemName;
  /** 对应商品表项目Id */
  @ApiModelProperty("对应商品表项目Id")
  private String oralIds;
  /** 对应商品表项目名称 */
  @ApiModelProperty("对应商品表项目名称")
  private String oralName;
}
