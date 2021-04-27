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
public class BillCategoryIncomeQuery extends PageQuery implements Serializable {
  /** 组织ID */
  @ApiModelProperty(value = "组织ID", required = true)
  @NotNull(message = "门诊ID不能为空！")
  private Integer orgId;
  /** 查询时间 */
  @ApiModelProperty(value = "查询时间", example = "yyyy-MM", required = true)
  @NotBlank(message = "查询时间不能为空！")
  private String queryDate;
  /** 优惠日期 */
  private String privilegeDate;
}
