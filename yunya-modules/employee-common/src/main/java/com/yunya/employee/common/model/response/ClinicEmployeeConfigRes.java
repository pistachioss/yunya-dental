package com.yunya.employee.common.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author bruce
 * @date 2020/7/21
 */
@Setter
@Getter
@ApiModel("门诊员工配置返回对象")
public class ClinicEmployeeConfigRes {
    @ApiModelProperty("助手ID")
    private Integer assistantEmployeeId;
    @ApiModelProperty("助手名称")
    private String assistantName;
    @ApiModelProperty("门诊科室ID")
    private Integer clinicDepartmentRoomId;
    @ApiModelProperty("门诊科室名称")
    private String clinicDepartmentRoomName;
    @ApiModelProperty("是否可预约 1 可预约")
    private Integer enableAppoint;
    @ApiModelProperty("是否可挂号 1 可挂号")
    private Integer enableRegistry;
}
