package com.yunya.feign.report.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 专科项目完成情况查询参数模型
 *
 * @author: chow
 * @date: 2021/1/15 19:20
 * @description:
 * @since: 1.0.0
 */
@ApiModel("专科项目完成情况查询参数模型")
@Data
@ToString
public class SpecialistProjectTargetCompletedInfoQuery implements Serializable {
  /** 组织ID */
  @ApiModelProperty(value = "组织ID", required = true)
  @NotNull(message = "组织ID不能为空！")
  private Integer orgId;
  /** 查询开始时间 */
  @ApiModelProperty(value = "查询开始时间", required = true)
  @NotBlank(message = "查询开始时间不能为空！")
  private String startDate;
  /** 查询结束时间 */
  @ApiModelProperty(value = "查询结束时间", required = true)
  @NotBlank(message = "查询结束时间不能为空！")
  private String endDate;
}
