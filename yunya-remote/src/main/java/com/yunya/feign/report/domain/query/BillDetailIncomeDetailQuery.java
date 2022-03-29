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
 * 简介: 项目收入明细查询参数模型
 *
 * @author: chow
 * @date: 2020/10/29 11:09
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("项目收入明细查询参数模型")
@EqualsAndHashCode(callSuper = true)
public class BillDetailIncomeDetailQuery extends PageQuery implements Serializable {
  /** 组织id列表 */
  @ApiModelProperty(value = "组织id列表", required = true)
  @NotNull(message = "组织ID列表不能为空！")
  private List<Integer> orgIds;
  /** 开单开始月份 */
  @ApiModelProperty(value = "开单开始月份", required = true, example = "yyyy-MM")
  @NotBlank(message = "开单开始月份不能为空！")
  private String startDate;
  /** 开单结束月份 */
  @ApiModelProperty(value = "开单结束月份", required = true, example = "yyyy-MM")
  @NotBlank(message = "开单结束月份不能为空！")
  private String endDate;
  /** 患者关键字（姓名、拼音姓名、手机号） */
  @ApiModelProperty("患者关键字（姓名、拼音姓名、手机号）")
  private String keyWord;
}
