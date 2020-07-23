package com.yunya.feign.employee_attend.form;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author 杨柳絮
 * @className EmployeeScheduleQueryForm
 * @description
 * @date 2020/7/10 9:47
 */
@Data
public class EmployeeScheduleQueryForm {
  /**
   * 姓名
   */
  private String name;

  /**
   * 岗位ID列表
   */
  private List<Integer> postNames;

  /**
   * 门诊ID
   */
  private Integer clinicId;

  /**
   * 开始时间
   */
  private Date startDate;

  /**
   * 截止时间
   */
  private Date endDate;

  private Integer page;

  private Integer size;

}
