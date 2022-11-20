package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 预约统计视图模型
 * @author: LHB
 * @create: 2021-01-08 15:33
 **/
@Data
@ApiModel(value = "AppointmentCountVO",description = "预约统计视图模型")
public class AppointmentCountVO implements Serializable {
    @ApiModelProperty("预约次数")
    private Integer appointedCount;
    @ApiModelProperty("履约次数")
    private Integer keepAppointmentCount;
    @ApiModelProperty("失约次数")
    private Integer missAppointmentCount;
    @ApiModelProperty("改约次数")
    private Integer changeAppointmentCount;
    @ApiModelProperty("取消预约次数")
    private Integer cancelAppointmentCount;
    @ApiModelProperty("就诊次数")
    private Integer treatmentCount;
}
