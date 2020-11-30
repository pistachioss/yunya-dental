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
 * 简介: 员工工作量开单明细
 *
 * @author: chow
 * @date: 2020/11/30 15:40
 * @description:
 * @since: 1.0.0
 */
@ApiModel("员工工作量开单明细")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class EmployeeOrderDetailWorkloadQuery extends PageQuery implements Serializable {
  /** 账单ID */
  @ApiModelProperty(value = "账单ID", required = true)
  @NotNull(message = "账单ID不能为空！")
  private Integer billId;
  /** 员工ID */
  @ApiModelProperty(value = "员工ID", required = true)
  @NotNull(message = "员工ID不能为空！")
  private Integer employeeId;
}
