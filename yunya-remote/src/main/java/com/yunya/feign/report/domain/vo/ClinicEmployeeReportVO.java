package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 报表员工VO
 *
 * @author: chow
 * @date: 2020/12/4 16:04
 * @description:
 * @since: 1.0.0
 */
@ApiModel("报表员工VO")
@Data
@ToString
public class ClinicEmployeeReportVO implements Serializable {
  /** 组织ID */
  @ApiModelProperty("组织ID")
  private Integer orgId;
  /** 组织名称 */
  @Excel(name = "门诊名称")
  @ApiModelProperty("门诊名称")
  private String abbreviation;
  /** 员工ID */
  @ApiModelProperty("员工ID")
  private Integer employeeId;
  /** 员工姓名 */
  @Excel(name = "员工")
  @ApiModelProperty("员工姓名")
  private String employeeName;
}
