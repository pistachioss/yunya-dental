package com.yunya.feign.employee_attend.vo;

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
  private String name;
  private Integer id;
}
