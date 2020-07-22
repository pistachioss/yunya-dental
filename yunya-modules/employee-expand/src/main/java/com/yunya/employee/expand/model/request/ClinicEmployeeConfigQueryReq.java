package com.yunya.employee.expand.model.request;

import io.swagger.annotations.ApiModel;
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
    private Integer clinicId;
    @NotNull(message = "员工不能为空")
    private Integer employeeId;

}
