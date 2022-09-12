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
public class EmployeeWorkloadDetailQuery extends PageQuery implements Serializable {
  /** 账单ID */
  @ApiModelProperty(value = "账单ID", required = true)
  private Integer billId;
  /** 收费记录ID */
  @ApiModelProperty("收费记录ID")
  private Integer billPayId;
  /** 员工ID */
  @ApiModelProperty(value = "员工ID")
  @NotNull(message = "员工ID不能为空！")
  private Integer employeeId;

  @ApiModelProperty(value = "是否查询咨询师", required = true, notes = "默认否，查询咨询师业绩时请传 'true'")
  private Boolean isConsulter = false;
}
