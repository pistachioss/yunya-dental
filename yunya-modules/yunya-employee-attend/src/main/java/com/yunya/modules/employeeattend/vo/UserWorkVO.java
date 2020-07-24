package com.yunya.modules.employeeattend.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author 杨柳絮 排班列表中人员信息以及各自的排班列表
 * @className UserWorkVO
 * @description
 * @date 2020/7/24 15:28
 */
@Data
@ToString
public class UserWorkVO {

  private Integer compEmpId;

  private String name;

  private String postNames;

  private List<WorkDayVO> days;
}
