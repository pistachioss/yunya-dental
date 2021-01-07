package com.yunya.feign.treatment.domain.query;

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
 * 简介: 门诊本月调整账单列表查询参数模型
 *
 * @author: chow
 * @date: 2021/1/6 14:14
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊本月调整账单列表查询参数模型")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class CurrentMonthBillAdjustQuery extends PageQuery implements Serializable {
  /** 组织ID */
  @ApiModelProperty(value = "组织ID", required = true)
  @NotNull(message = "组织ID不能为空！")
  private Integer orgId;
  /** 查询日期 */
  @ApiModelProperty(value = "查询日期", required = true, example = "yyyy-MM")
  @NotBlank(message = "查询日期不能为空！")
  private String currentMonth;
}
