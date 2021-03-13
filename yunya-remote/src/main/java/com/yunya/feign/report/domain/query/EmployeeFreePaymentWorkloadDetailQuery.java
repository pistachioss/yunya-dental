package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 员工工作量明细参数
 *
 * @author: chow
 * @date: 2020/11/30 15:40
 * @description:
 * @since: 1.0.0
 */
@ApiModel("员工工作量明细参数")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class EmployeeFreePaymentWorkloadDetailQuery extends PageQuery implements Serializable {
  /** 账单ID */
  @ApiModelProperty(value = "账单ID", required = true)
  @NotNull(message = "账单ID不能为空！")
  private Integer billId;

  /** 账单收费ID */
  @ApiModelProperty(value = "账单收费ID", required = true)
  @NotNull(message = "账单收费ID不能为空！")
  private Integer billPayId;

  @ApiModelProperty("员工id")
  @NotNull(message = "员工id不能为空！")
  private Integer employeeId;

  /** 组织ID */
  @ApiModelProperty(value = "组织ID")
  @NotNull(message = "组织ID不能为空！")
  private Integer orgId;
}
