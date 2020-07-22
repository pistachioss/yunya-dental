package com.yunya.modules.employeeattend.form;

import lombok.Data;

import java.util.Date;

/**
 * @author 杨柳絮
 * @className EmployeeScheduleForm
 * @description
 * @date 2020/7/10 10:52
 */
@Data
public class EmployeeScheduleForm {
  private String userId;

  private String scheduleId;

  private Date WorkDate;

  /**
   * 公司端对应的诊所ID
   */
  private Integer clinicId;
}
