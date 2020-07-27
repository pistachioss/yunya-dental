package com.yunya.models.appointment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 可预约项目列表
 */
@Data
@ApiModel("可预约项目列表")
public class AppointmentItemEnableModel {
    @ApiModelProperty("门诊ID")
    private Integer orgId;

    @ApiModelProperty("预约类型")
    private String typeName;
    @ApiModelProperty("可预约项目")
    private List<AppointmentItemType> appointmentItemTypes;

}