package com.yunya.modules.employeeattend.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;
import java.util.Map;

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
