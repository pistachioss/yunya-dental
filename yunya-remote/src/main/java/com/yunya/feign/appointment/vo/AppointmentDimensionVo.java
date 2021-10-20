package com.yunya.feign.appointment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 预约可视图模型（患者维度）
 *
 * @author yunya-lihuibin
 * @create 2020-08-10 19:59
 * @update yunya-lihuibin    2020-08-10    新建
 */
@ApiModel(value = "AppointmentDimensionVo", description = "预约可视图模型（患者维度）")
@Data
@ToString
public class  AppointmentDimensionVo implements Serializable {

    /** 医生id */
    @ApiModelProperty(value = "医生id")
    private Integer dentistId;

    /** 医生名字 */
    @ApiModelProperty(value = "医生名字")
    private String name;

    /** 预约患者数量 */
    @ApiModelProperty(value = "预约患者数量")
    private Integer patientNum;

    /** 日期 */
    @ApiModelProperty(value = "日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date currentDate;

    /** 预约可视图患者卡片模型 */
    @ApiModelProperty(value = "预约可视图患者卡片")
    private List<AppointmentPatientCardVo> appointmentPatientCardVos;

//    /** 医生排班表卡片模型 */
//    @ApiModelProperty(value = "医生排班表卡片")
//    private List<WorkDayVO> dentistScheduleVos;

    /** 医生排班表卡片模型 */
    @ApiModelProperty(value = "医生排班表卡片")
    private List<EmpScheduleVo> dentistScheduleVos;

    /** 分解助手信息 */
    @ApiModelProperty(value = "分解助手信息")
    private List<AppointmentDimensionVo> appointmentAssistants;

    @ApiModelProperty("0-无排班 1-有排班 2-请假 3-加班 4-外勤")
    private Integer type;

//    /** 员工请假信息 */
//    @ApiModelProperty(value = "员工请假信息")
//    private List<EmpScheduleVo> leaveInfos;

    /** 医生在职状态: true-在职，false-离职*/
    @ApiModelProperty("医生在职状态: true-在职，false-离职")
    private Boolean workStatus = true;
}
