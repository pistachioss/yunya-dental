package com.yunya.feign.clinic_base.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

/**
 * 简介: 专科数量分解保存参数模型
 *
 * @author: chow
 * @date: 2020/12/23 11:02
 * @description:
 * @since: 1.0.0
 */
@ApiModel("专科数量分解保存参数模型")
@Data
@ToString
public class SpecialistProjectTargetModel implements Serializable {
  /** 业务类型 */
  @ApiModelProperty(value = "专科项目ID", required = true)
  @NotNull(message = "专科项目ID不能为空！")
  private Integer specialistProjectId;
  /** 年份 */
  @ApiModelProperty(value = "添加年份", required = true, example = "2020")
  @NotBlank(message = "添加年份不能为空！")
  private String businessYear;
  /** 数据所属类型 */
  @ApiModelProperty(value = "数据所属类型", required = true, example = "0-组织；1-个人")
  @NotNull(message = "数据所属类型不能为空！")
  private Byte belongType;
  /** 单位 */
  @ApiModelProperty(value = "单位", required = true)
  @NotBlank(message = "业务目标单位不能为空！")
  private String unit;
  /** 月业务目标参数模型 */
  private Set<TargetOfMonthModel> specialistProjectTargetOfMonthModels;
}
