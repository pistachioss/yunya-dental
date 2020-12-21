package com.yunya.feign.report.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 对账单统计查询参数
 *
 * @author: chow
 * @date: 2020/12/18 10:41
 * @description:
 * @since: 1.0.0
 */
@ApiModel("对账单统计查询参数")
@Data
@ToString
public class StatementStatisticQuery implements Serializable {
  /** 组织ID */
  @ApiModelProperty(value = "组织ID", required = true)
  @NotNull(message = "组织ID不能为空！")
  private Integer orgId;
  /** 查询月份 */
  @ApiModelProperty(value = "查询月份", required = true)
  @NotBlank(message = "查询月份不能为空！")
  private String queryDate;
}
