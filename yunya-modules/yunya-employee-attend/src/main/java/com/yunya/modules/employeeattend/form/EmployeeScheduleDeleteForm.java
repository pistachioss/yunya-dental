package com.yunya.modules.employeeattend.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author 杨柳絮
 * @className EmployeeScheduleDeleteForm
 * @description
 * @date 2020/7/9 15:27
 */
@Data
public class EmployeeScheduleDeleteForm implements Serializable {
  /**
   * 员工ID
   */
  @NotBlank(message = "员工ID不允许为空")
  @ApiModelProperty("员工ID")
  private Integer employeeId;

  /**
   * 公司端排班表ID
   */
  @NotBlank(message = "公司端排班表ID不允许为空")
  @ApiModelProperty("公司端排班表ID")
  private Integer scheduleId;

  /**
   * 排班日期
   */
  @NotNull(message = "排班日期不允许为空")
  @ApiModelProperty("排班日期")
  private String workDateString;

  /**
   * 门诊ID
   */
  @NotNull(message = "门诊ID不允许为空")
  @ApiModelProperty("门诊ID")
  private Integer clinicId;
}
