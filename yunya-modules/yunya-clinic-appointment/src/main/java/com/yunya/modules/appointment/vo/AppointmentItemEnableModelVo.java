package com.yunya.modules.appointment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 可预约项目列表
 */
@Data
@ApiModel("可预约项目列表")
public class AppointmentItemEnableModelVo implements Serializable {
    @ApiModelProperty("门诊ID")
    private Integer orgId;

    @ApiModelProperty("预约类型")
    private String typeName;
    @ApiModelProperty("可预约项目")
    private List<AppointmentItemVo> appointmentItemTypes;

}