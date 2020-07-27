package com.yunya.modules.employee.expand.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author bruce
 * @date 2020/7/10
 */
@Data
@ApiModel("门诊员工配置模型")
public class ClinicEmployeeConfigReq {
    /**
     * 门诊科室ID
     */
    @ApiModelProperty(value = "门诊科室ID")
    private Integer clinicDepartmentRoomId;
    /**
     * 助手ID
     */
    @ApiModelProperty(value = "助手ID")
    private Integer assistantEmployeeId;
    /**
     * 是否可预约
     */
    @ApiModelProperty(value = "是否可预约")
    private Boolean enableAppoint;
    /**
     * 是否可挂号
     */
    @ApiModelProperty(value = "是否可挂号")
    private Boolean enableRegistry;

    @ApiModelProperty(value = "门诊ID")
    @NotNull
    private Integer clinicId;
}
