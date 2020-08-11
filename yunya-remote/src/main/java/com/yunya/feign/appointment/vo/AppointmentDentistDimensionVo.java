package com.yunya.feign.appointment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 预约可视图模型（患者维度）
 *
 * @author yunya-lihuibin
 * @create 2020-08-10 19:59
 * @update yunya-lihuibin    2020-08-10    新建
 */
@ApiModel(value = "预约可视图模型（患者维度）")
@Data
@ToString
public class AppointmentDentistDimensionVo implements Serializable {

    /** 医生id */
    @ApiModelProperty(value = "医生id")
    private Integer userId;

    /** 医生名字 */
    @ApiModelProperty(value = "医生名字")
    private String name;

    /** 预约患者数量 */
    @ApiModelProperty(value = "预约患者数量")
    private Integer patientNum;

    /** 日期 */
    @ApiModelProperty(value = "日期")
    private Date date;

    /** 预约可视图患者卡片模型 */
    @ApiModelProperty(value = "预约可视图患者卡片")
    private AppointmentPatientCardVo appointmentPatientCardVo;

    /** 医生排班表卡片模型 */
    @ApiModelProperty(value = "医生排班表卡片")
    private DentistScheduleVo dentistScheduleVo;
}
