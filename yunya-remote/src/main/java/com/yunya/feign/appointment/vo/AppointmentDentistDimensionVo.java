package com.yunya.feign.appointment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 预约可视图医生维度视图模型(废弃)
 * @author yunya-lihuibin
 * @create 2020-08-12 14:18
 * @update yunya-lihuibin    2020-08-12    新建
 */
@ApiModel(value = "预约可视图医生维度视图模型(废弃)")
@Data
@ToString
@Deprecated
public class AppointmentDentistDimensionVo implements Serializable {

    /** 主治医师信息 */
    @ApiModelProperty(value = "主治医师信息")
    private AppointmentDimensionVo appointmentDentistInfoVo;

    /** 分解助手信息 */
    @ApiModelProperty(value = "分解助手信息")
    private List<AppointmentDimensionVo> appointmentAssistants;
}
