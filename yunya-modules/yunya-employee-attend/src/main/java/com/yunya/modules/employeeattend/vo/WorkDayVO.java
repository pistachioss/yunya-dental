package com.yunya.modules.employeeattend.vo;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author 杨柳絮 排班列表中排班信息
 * @className WorkDayVO
 * @description
 * @date 2020/7/24 15:30
 */
@Data
@ToString
public class WorkDayVO {
  private Date date;
  private Integer compClinId;
  /**
   * 班次名
   */
  private String name;
  /**
   * 时间段
   */
  private String stime;
  private Integer id;
  /**
   * 班次属性
   */
  private String type;
}
