package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Collection;

/**
 * 简介: 收费项目工作量统计参数
 *
 * @author: chow
 * @date: 2021/4/1 09:26
 * @description:
 * @since: 1.0.0
 */
@ApiModel("收费项目工作量统计参数")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class BillItemTollAndWorkloadQuery extends PageQuery implements Serializable {
  /** 门诊ID */
  @ApiModelProperty(value = "门诊ID")
  private Integer orgId;
  /** 查询时间 */
  @ApiModelProperty(value = "查询开始时间", required = true)
  @NotBlank(message = "查询开始时间不能为空！")
  private String startDate;
  /** 查询结束时间 */
  @ApiModelProperty(value = "查询结束时间", required = true)
  @NotBlank(message = "查询结束时间不能为空！")
  private String endDate;
  /** 分类及项目列表 */
  @ApiModelProperty(value = "分类及项目列表")
  private Collection<Integer[]> categoryItems;
  /** 项目分类ID列表 */
  private Collection<Integer> categoryIds;
  /** 项目ID列表 */
  private Collection<Integer> itemIds;
  /** 员工ID列表 */
  @ApiModelProperty(value = "员工ID列表")
  private Integer[] employeeIds;
  /** 就职状态列表 */
  @ApiModelProperty(value = "就职状态列表")
  private Integer[] workStatus;
}
