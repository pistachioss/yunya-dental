package com.yunya.feign.clinic_base.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 专科项目数量分解查询参数模型
 *
 * @author: chow
 * @date: 2020/12/23 12:35
 * @description:
 * @since: 1.0.0
 */
@ApiModel("专科项目数量分解查询参数模型")
@Data
@ToString
public class SpecialistProjectTargetQuery implements Serializable {
  /** 专科项目ID */
  @ApiModelProperty(value = "专科项目ID", required = true)
  @NotNull(message = "专科项目ID不能为空！")
  private Integer specialistProjectId;
  /** 年份 */
  @ApiModelProperty(value = "添加年份", required = true, example = "2020")
  @NotBlank(message = "添加年份不能为空！")
  private String businessYear;
  /** 数据所属类型 */
  @ApiModelProperty(value = "数据所属类型", required = true, example = "0-组织；1-个人")
  @NotNull(message = "数据所属类型不能为空！0-组织；1-个人")
  private Byte belongType;
  /** 数据所属ID（组织ID或个人ID） */
  @ApiModelProperty(value = "数据所属ID（组织ID或个人ID）", required = true)
  @NotNull(message = "数据所属ID不能为空！")
  private Integer belongId;
}
