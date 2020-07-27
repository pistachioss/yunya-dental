package com.yunya.feign.employee_attend.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author 杨柳絮
 * @className EmployeeScheduleResultVO
 * @description
 * @date 2020/7/24 15:17
 */
@Data
@ToString
public class EmployeeScheduleResultVO {

  private List<UserWorkVO> shiftWorkDatas;

  private Integer count;
}
