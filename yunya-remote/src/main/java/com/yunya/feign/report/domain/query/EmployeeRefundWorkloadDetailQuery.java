package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 员工退费工作量退费明细查询参数
 *
 * @author: chow
 * @date: 2020/12/1 20:23
 * @description:
 * @since: 1.0.0
 */
@ApiModel("员工退费工作量退费明细查询参数")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class EmployeeRefundWorkloadDetailQuery extends PageQuery implements Serializable {
  /** 退费记录ID */
  @ApiModelProperty("退费记录ID")
  private Integer refundId;
  /** 员工ID */
  @ApiModelProperty("员工ID")
  private Integer employeeId;

  @ApiModelProperty(value = "是否查询咨询师", required = true, notes = "默认否，查询咨询师业绩时请传 'true'")
  private Boolean isConsulter = false;
}
