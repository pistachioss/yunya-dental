package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;

/**
 * 简介: 员工工作量查询参数模型
 *
 * @author: chow
 * @date: 2020/10/29 14:52
 * @description:
 * @since: 1.0.0
 */
@ApiModel("员工工作量查询参数模型")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class EmployeeWorkloadQuery extends PageQuery implements Serializable {
  /** 组织ID */
  @ApiModelProperty(value = "组织id", required = true)
  @NotNull(message = "组织ID不能为空！")
  private Integer orgId;
  /** 查询结束时间 */
  @ApiModelProperty(value = "时间类型:0-月；1-年；2-日", required = true)
  @NotNull(message = "时间类型不能为空！")
  private Byte dateType;
  /** 查询时间 */
  @ApiModelProperty(value = "查询时间", required = true)
  @NotBlank(message = "查询时间不能为空！")
  private String queryDate;
  /** 员工ID列表 */
  @ApiModelProperty("员工ID列表")
  private Integer[] employeeIds;
  /** 岗位id列表 */
  @ApiModelProperty("岗位id列表")
  private Integer[] postIds;
  /** 就职状态列表：0-试用；1-正式；2-离职；3-实习 */
  @ApiModelProperty("就职状态列表：0-试用；1-正式；2-离职；3-实习")
  private Integer[] workStatus;
  /** 账单ID列表 */
  @ApiModelProperty("账单ID列表")
  private Collection<Integer> billIds;
}
