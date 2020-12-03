package com.yunya.modules.employeeattend.form;

import io.swagger.annotations.ApiModelProperty;
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
  @ApiModelProperty("开始时间")
  private String startDate;

  /**
   * 结束时间
   */
  @ApiModelProperty("结束时间")
  private String endDate;

  /**
   * 目标开始时间
   */
  @ApiModelProperty("目标开始时间")
  private String targetStartDate;

  /**
   * 目标结束时间
   */
  @ApiModelProperty("目标结束时间")
  private String targetEndDate;

  /**
   * 需要复制排班的员工ID列表
   */
  @ApiModelProperty("需要复制排班的员工ID列表")
  private List<Integer> employeeIdLIst;

  /**
   * 门诊ID
   */
  @ApiModelProperty("门诊ID")
  @NotNull(message = "门诊ID不能为空")
  private String clinicId;

}
