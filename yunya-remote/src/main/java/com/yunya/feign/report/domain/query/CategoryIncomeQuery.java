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
import java.util.List;

/**
 * 简介: 分类收入汇总列表查询参数模型
 *
 * @author: chow
 * @date: 2020/11/24 20:15
 * @description:
 * @since: 1.0.0
 */
@ApiModel("分类收入汇总列表查询参数模型")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class CategoryIncomeQuery extends PageQuery implements Serializable {
  /** 组织ID列表 */
  @ApiModelProperty(value = "组织ID列表", required = true)
  @NotNull(message = "门诊ID列表不能为空！")
  private List<Integer> orgIds;
  /** 查询开始时间 */
  @ApiModelProperty(value = "查询开始时间", example = "yyyy-MM", required = true)
  @NotBlank(message = "查询开始时间不能为空！")
  private String startDate;
  /** 查询结束时间 */
  @ApiModelProperty(value = "查询结束时间", example = "yyyy-MM", required = true)
  @NotBlank(message = "查询结束时间不能为空！")
  private String endDate;
  /** 优惠日期 */
  private String privilegeDate;
}
