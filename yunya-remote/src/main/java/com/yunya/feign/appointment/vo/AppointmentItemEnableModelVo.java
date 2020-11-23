package com.yunya.feign.appointment.vo;

import com.yunya.feign.appointment.vo.AppointmentItemVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 可预约项目列表
 */
@Data
@ToString
@ApiModel("可预约项目列表")
public class AppointmentItemEnableModelVo implements Serializable {
    @ApiModelProperty("门诊ID")
    private Integer orgId;

    @ApiModelProperty(value = "预约项目分类ID")
    private Integer typeId;

    @ApiModelProperty(value = "预约项目分类色值(十六进制)")
    private String bgColor;

    @ApiModelProperty("预约类型")
    private String typeName;

    @ApiModelProperty("可预约项目")
    private List<AppointmentItemVo> appointmentItems;

}