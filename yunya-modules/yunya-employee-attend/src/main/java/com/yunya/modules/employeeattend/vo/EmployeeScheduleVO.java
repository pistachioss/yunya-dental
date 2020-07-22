package com.yunya.modules.employeeattend.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author 杨柳絮
 * @className EmployeeScheduleVO
 * @description
 * @date 2020/7/13 10:46
 */
@Data
public class EmployeeScheduleVO {
  /**
   * 用户id
   */
  private Integer employeeId;
  /**
   * 门诊id
   */
  private Integer clinicId;
  /**
   * 排班表ID
   */
  private Integer scheduleId;
  /**
   * 工作日
   */
  private Date workDate;
}
