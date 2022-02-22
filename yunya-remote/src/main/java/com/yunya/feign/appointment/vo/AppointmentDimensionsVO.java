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
    /** 日期 */
    @ApiModelProperty(value = "日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date currentDate;
    @ApiModelProperty(value = "预约可视图模型(不同医生的组合)")
    private List<AppointmentDimensionVo> appointmentDimensionVoList;
}
