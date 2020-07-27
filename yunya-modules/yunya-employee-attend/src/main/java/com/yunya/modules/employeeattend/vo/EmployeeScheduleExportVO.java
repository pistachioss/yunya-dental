package com.yunya.modules.employeeattend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.framework.common.annation.Excel;
import com.yunya.framework.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 杨柳絮
 * @className EmployeeScheduleExportVO
 * @description
 * @date 2020/7/22 12:51
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class EmployeeScheduleExportVO extends BaseEntity implements Serializable {

  @Excel(name = "员工")
  private String name;


  @Excel(name = "日期(被复制)")
  private String copy_date;

  @Excel(name = "排班公司/门诊(被复制)")
  private String copy_company_name;

  @Excel(name = "班次(被复制)")
  private String copy_schedule;


  @Excel(name = "日期(覆盖)")
  private String cover_date;

  @Excel(name = "排班公司/门诊(覆盖)")
  private String cover_company_name;

  @Excel(name = "班次(覆盖)")
  private String cover_schedule;

}
