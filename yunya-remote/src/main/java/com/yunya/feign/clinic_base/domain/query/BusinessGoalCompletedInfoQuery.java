package com.yunya.feign.clinic_base.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 简介: 业务目标完成情况查询参数模型
 *
 * @author: chow
 * @date: 2021/1/14 19:59
 * @description:
 * @since: 1.0.0
 */
@ApiModel("业务目标完成情况查询参数模型")
@Data
@ToString
public class BusinessGoalCompletedInfoQuery implements Serializable {
  /** 业务目标类型 */
  @ApiModelProperty(value = "业务目标类型", required = true, example = "0-营业收入；1-工作量；2-初诊人数；3-就诊人次")
  @NotNull(message = "业务目标类型不能为空！")
  private Byte businessType;
  /** 组织ID */
  @ApiModelProperty(value = "组织ID", required = true)
  @NotNull(message = "组织ID不能为空！")
  private Integer orgId;
  /** 查询开始时间 */
  @ApiModelProperty(value = "查询开始时间", required = true, example = "yyyy-MM")
  @NotBlank(message = "查询开始时间不能为空！")
  private String startDate;
  /** 查询结束时间 */
  @ApiModelProperty(value = "查询结束时间", required = true, example = "yyyy-MM")
  @NotBlank(message = "查询结束时间不能为空！")
  private String endDate;
  /** 组织id列表 */
  @ApiModelProperty(value = "组织id列表")
  private Integer[] orgIds;
  @ApiModelProperty(value = "查询日期方式")
  private Byte dateType;
  /** 日期列表*/
  @ApiModelProperty(value = "日期列表")
  private List<String> dateRange;
  /** 业务目标类型列表 */
  @ApiModelProperty(value = "业务目标类型列表", example = "0-营业收入；1-工作量；2-初诊人数；3-就诊人次")
  private Byte[] businessTypes;
}
