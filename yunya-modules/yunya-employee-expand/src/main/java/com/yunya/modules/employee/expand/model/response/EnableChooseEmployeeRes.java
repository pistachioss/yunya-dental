package com.yunya.modules.employee.expand.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author bruce
 * @date 2020/7/23
 */
@Setter
@Getter
@ApiModel("员工、助手、科室对象")
public class EnableChooseEmployeeRes {
    @ApiModelProperty("员工ID")
    private Integer employeeId;
    @ApiModelProperty("员工名称")
    private String employeeName;
    @ApiModelProperty("助手ID")
    private Integer assistantEmployeeId;
    @ApiModelProperty("助手名称")
    private String assistantName;
    @ApiModelProperty("门诊科室ID")
    private Integer clinicDepartmentRoomId;
    @ApiModelProperty("门诊科室名称")
    private String clinicDepartmentRoomName;
    /** 员工在职状态 */
    @ApiModelProperty("员工在职状态：0-试用期；1-正式；2-离职")
    private Byte workStatus;
}
