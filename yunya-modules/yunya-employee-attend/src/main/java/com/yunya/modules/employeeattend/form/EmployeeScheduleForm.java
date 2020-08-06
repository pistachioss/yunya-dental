package com.yunya.modules.employeeattend.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author 杨柳絮
 * @className EmployeeScheduleForm
 * @description
 * @date 2020/7/10 10:52
 */
@Data
public class EmployeeScheduleForm {

  @NotNull(message = "员工ID不允许为空")
  @ApiModelProperty("员工ID")
  private String userId;

  @NotNull(message = "排班ID不允许为空")
  @ApiModelProperty("排班ID")
  private String scheduleId;

  @NotNull(message = "排班日期不允许为空")
  @ApiModelProperty("排班日期")
  private String WorkDate;

  /**
   * 公司端对应的诊所ID
   */
  @NotNull(message = "员工ID不允许为空")
  @ApiModelProperty("公司端对应的诊所ID")
  private Integer clinicId;
}
