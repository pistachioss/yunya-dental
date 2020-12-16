package com.yunya.modules.employeeattend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
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
   * id
   */
  private Integer id;
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
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date workDate;
  /**
   * 班次属性
   */
  private String type;
  
  /** 班次名称 */
  private String name;
  
  /** 开始时间点1 */
  private Date firstStartTime;

  /** 结束时间点1 */
  private Date firstEndTime;

  /** 开始时间点2 */
  private Date secondStartTime;

  /** 结束时间点2 */
  private Date secondEndTime;
}
