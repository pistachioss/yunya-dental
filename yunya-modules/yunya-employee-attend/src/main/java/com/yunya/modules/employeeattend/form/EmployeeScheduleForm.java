package com.yunya.modules.employeeattend.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @author 杨柳絮
 * @className EmployeeScheduleForm
 * @description
 * @date 2020/7/10 10:52
 */
@Data
public class EmployeeScheduleForm {

  @NotBlank(message = "员工ID不允许为空")
  @ApiModelProperty("员工ID")
  private String userId;

  @NotBlank(message = "排班ID不允许为空")
  @ApiModelProperty("排班ID")
  private String scheduleId;

  @NotBlank(message = "排班日期不允许为空")
  @ApiModelProperty("排班日期")
  private Date WorkDate;

  /**
   * 公司端对应的诊所ID
   */
  @NotBlank(message = "员工ID不允许为空")
  @ApiModelProperty("公司端对应的诊所ID")
  private Integer clinicId;
}
