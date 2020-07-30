package com.yunya.modules.employeeattend.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author 杨柳絮
 * @className EmployeeScheduleQueryForm
 * @description
 * @date 2020/7/10 9:47
 */
@Data
public class EmployeeScheduleQueryForm implements Serializable {

  /**
   * 医生或助手id
   */
  @ApiModelProperty("医生或助手id")
  private Integer userId;
  /**
   * 姓名
   */
  @ApiModelProperty("姓名")
  private String name;

  /**
   * 岗位ID列表
   */
  @ApiModelProperty("岗位ID列表")
  private List<Integer> postNames;

  /**
   * 门诊ID
   */
  @ApiModelProperty("门诊ID")
  private Integer clinicId;

  /**
   * 开始时间
   */
  @ApiModelProperty("开始时间")
  private String startDate;

  /**
   * 截止时间
   */
  @ApiModelProperty("截止时间")
  private String endDate;

  @ApiModelProperty("页数")
  private Integer page;

  @ApiModelProperty("每页个数")
  private Integer size;

}
