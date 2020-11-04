package com.yunya.modules.employee.expand.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;


/**
 * @author bruce
 * @date 2020/7/21
 */
@Setter
@Getter
@ApiModel("门诊员工配置查询参数")
public class ClinicEmployeeConfigQueryReq {

    @NotNull(message = "门诊不能为空")
    @ApiModelProperty(value = "门诊id", required = true)
    private Integer clinicId;
    @ApiModelProperty(value = "员工id", required = true)
    @NotNull(message = "员工不能为空")
    private Integer employeeId;

}
