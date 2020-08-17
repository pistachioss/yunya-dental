package com.yunya.feign.appointment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 条件查询预约列表参数
 *
 * @author yunya-lihuibin
 * @create 2020-08-03 20:21
 * @update yunya-lihuibin    2020-08-03    新建
 */
@ApiModel(value = "条件查询预约列表参数")
@Data
@ToString
public class AppointmentQuery implements Serializable {

    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    private Integer patientId;

    /**
     * 诊所ID
     */
    @ApiModelProperty(value = "诊所ID")
    private Integer orgId;

    /**
     * 医生ID
     */
    @ApiModelProperty(value = "医生ID")
    private Integer dentistId;

    /**
     * 助手ID 默认医生配置助手ID
     */
    @ApiModelProperty(value = "助手ID 默认医生配置助手ID")
    private Integer assistantId;

    /**
     * 门诊科室ID 默认医生配置科室ID
     */
    @ApiModelProperty(value = "门诊科室ID 默认医生配置科室ID")
    private Integer deptRoomId;

    /**
     * 门诊设备ID
     */
    @ApiModelProperty(value = "门诊设备ID")
    private Integer clinicDeviceItemId;

    /**
     * 预约项目ID
     */
    @ApiModelProperty(value = "预约项目ID")
    private Integer clinicAppointItemId;

    /**
     * 预约日期
     */
    @ApiModelProperty(value = "预约日期")
    private Date appointDate;

    /**
     * 预约时间
     */
    @ApiModelProperty(value = "预约时间")
    private String appointTime;

    /**
     * 预约开始时间
     */
    @ApiModelProperty(value = "预约开始时间")
    private Date appointStartTime;

    /**
     * 预约结束时间
     */
    @ApiModelProperty(value = "预约结束时间")
    private Date appointEndTime;

    /**
     * 预约时间段 预约开始时间-预约结束时间
     */
    @ApiModelProperty(value = "预约时间段 预约开始时间-预约结束时间")
    private String appointPeriod;

    /**
     * 预约类型 0-初诊预约；1-复诊预约
     */
    @ApiModelProperty(value = "预约类型 0-初诊预约；1-复诊预约")
    private Byte appointType;

    /**
     * 预约确认 0-未确认；1-确认
     */
    @ApiModelProperty(value = "预约确认 0-未确认；1-确认")
    private Boolean confirmStatus;

    /**
     * 预约状态 0-预约未到，1-履约，2，取消预约，3-失约
     */
    @ApiModelProperty(value = "预约状态 0-预约未到，1-履约，2，取消预约，3-失约")
    private Byte appointStatus;
}
