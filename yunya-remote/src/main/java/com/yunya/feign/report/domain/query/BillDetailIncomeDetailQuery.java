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
  /** 组织ID */
  @ApiModelProperty(value = "组织id", required = true)
  @NotNull(message = "组织ID不能为空！")
  private Integer orgId;
  /** 开单日期 */
  @ApiModelProperty(value = "开单日期", required = true, example = "yyyy-MM")
  @NotBlank(message = "开单日期不能为空！")
  private String orderDate;
  /** 患者关键字（姓名、拼音姓名、手机号） */
  @ApiModelProperty("患者关键字（姓名、拼音姓名、手机号）")
  private String keyWord;
}
