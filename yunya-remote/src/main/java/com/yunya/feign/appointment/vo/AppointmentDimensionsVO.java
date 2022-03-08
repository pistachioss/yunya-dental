package com.yunya.feign.appointment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/2/22
 * @description: 预约可视图模型（医生可多选）
 */
@Data
public class AppointmentDimensionsVO {
    /** 医生id */
    @ApiModelProperty(value = "医生id")
    private Integer dentistId;
    /** 医生名字 */
    @ApiModelProperty(value = "医生名字")
    private String name;
    @ApiModelProperty(value = "预约可视图模型(不同医生的组合)")
    private List<AppointmentDimensionVo> appointmentDimensionVoList;
}
