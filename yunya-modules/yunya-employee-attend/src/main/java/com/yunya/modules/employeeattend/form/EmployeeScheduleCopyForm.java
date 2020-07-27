package com.yunya.modules.employeeattend.form;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author 杨柳絮
 * @className EmployeeScheduleCopyForm
 * @description 复制排班表
 * @date 2020/7/9 16:40
 */
@Data
public class EmployeeScheduleCopyForm {
  /**
   * 开始时间
   */
  @NotNull(message = "开始时间不能为空")
  private Date startDate;

  /**
   * 结束时间
   */
  private Date endDate;

  /**
   * 目标开始时间
   */
  private Date targetStartDate;

  /**
   * 目标结束时间
   */
  private Date targetEndDate;

  /**
   * 需要复制排班的员工ID列表
   */
  private List<Integer> employeeIdLIst;

  /**
   * 门诊ID
   */
  private String clinicId;
}
