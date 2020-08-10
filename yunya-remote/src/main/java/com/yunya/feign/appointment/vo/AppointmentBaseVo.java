package com.yunya.feign.appointment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 患者预约基础信息
 *
 * @author yunya-lihuibin
 * @create 2020-07-28 13:11
 * @update yunya-lihuibin    2020-07-28    新建
 */
@ApiModel("患者预约基础信息")
@Data
@ToString
public class AppointmentBaseVo implements Serializable {
    /** 主键 */
    @ApiModelProperty("主键")
    private Integer id ;
    /** 患者ID */
    @ApiModelProperty(value = "患者ID")
    private Integer patientId ;
    /** 诊所ID */
    @ApiModelProperty("诊所ID")
    private Integer orgId ;
    /** 医生ID */
    @ApiModelProperty(value = "医生ID")
    private Integer dentistId ;
    /** 助手ID;默认医生配置助手ID */
    @ApiModelProperty("助手ID;默认医生配置助手ID")
    private Integer assistantId ;
    /** 门诊科室ID;默认医生配置科室ID */
    @ApiModelProperty("门诊科室ID;默认医生配置科室ID")
    private Integer clinicDeptRoomId ;
    /** 门诊设备ID */
    @ApiModelProperty("门诊设备ID")
    private Integer clinicDeviceItemId ;
    /** 预约项目ID */
    @ApiModelProperty("预约项目ID")
    private Integer clinicAppointItemId ;
    /** 预约总时长;默认取预约项目时长 */
    @ApiModelProperty(value = "预约总时长;默认取预约项目时长")
    private Integer appointDuration ;
    /** 预约日期 */
    @ApiModelProperty(value = "预约日期")
    private Date appointDate ;
    /** 预约时间 */
    @ApiModelProperty(value = "预约时间")
    private String appointTime ;
    /** 预约开始时间 */
    @ApiModelProperty(value = "预约开始时间")
    private Date appointStartTime ;
    /** 预约结束时间 */
    @ApiModelProperty(value = "预约结束时间")
    private Date appointEndTime ;
    /** 预约时间段;预约开始时间-预约结束时间 */
    @ApiModelProperty(value = "预约时间段;预约开始时间-预约结束时间")
    private String appointPeriod ;
    /** 牙位 */
    @ApiModelProperty(value = "牙位")
    private String toothBit ;
    /** 预约内容 */
    @ApiModelProperty(value = "预约内容")
    private String appointContent ;
    /** 预约类型;0-初诊预约；1-复诊预约 */
    @ApiModelProperty(value = "预约类型;0-初诊预约；1-复诊预约")
    private Integer appointType ;
    /** 预约确认;0-未确认；1-确认 */
    @ApiModelProperty(value = "预约确认;0-未确认；1-确认")
    private String confirmStatus ;
    /** 预约状态;0-预约未到，1-履约，2，取消预约，3-失约 */
    @ApiModelProperty(value = "预约状态;0-预约未到，1-履约，2，取消预约，3-失约")
    private Integer appointStatus ;
    /** 备注 */
    @ApiModelProperty(value = "备注")
    private String remarks ;
    /** 是否启用 */
    @ApiModelProperty(value = "是否启用")
    private Boolean inservice ;
}
