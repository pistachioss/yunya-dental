package com.yunya.feign.appointment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.feign.appointment.domain.base.AppointmentSplitBaseInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 预约列表视图模型
 *
 * @author yunya-lihuibin
 * @create 2020-08-03 20:21
 * @update yunya-lihuibin    2020-08-03    新建
 */
@ApiModel(value = "AppointmentVo", description = "预约列表视图模型")
@Data
@ToString
public class AppointmentVo implements Serializable {
    /**
     * 预约id
     */
    @ApiModelProperty(value = "预约id")
    private Integer id;

    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    private Integer patientId;

    /**
     * 患者名字
     */
    @ApiModelProperty(value = "患者名字")
    private String patientName;

    /**
     * 患者手机号
     */
    @ApiModelProperty(value = "患者手机号")
    private String patientMobile;

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
     * 医生名字
     */
    @ApiModelProperty(value = "医生名字")
    private String dentistName;

    /**
     * 助手ID 默认医生配置助手ID
     */
    @ApiModelProperty(value = "助手ID 默认医生配置助手ID")
    private Integer assistantId;

    /**
     * 助手名字
     */
    @ApiModelProperty(value = "助手名字")
    private String assistantName;

    /**
     * 门诊科室ID 默认医生配置科室ID
     */
    @ApiModelProperty(value = "门诊科室ID 默认医生配置科室ID")
    private Integer deptRoomId;

    /**
     * 门诊科室名称
     */
    @ApiModelProperty(value = "门诊科室名称")
    private String deptRoomName;

    /**
     * 门诊设备ID
     */
    @ApiModelProperty(value = "门诊设备ID")
    private Integer clinicDeviceItemId;

    /**
     * 门诊设备名称
     */
    @ApiModelProperty(value = "门诊设备名称")
    private String clinicDeviceItemName;

    /**
     * 预约项目ID
     */
    @ApiModelProperty(value = "预约项目ID")
    private Integer clinicAppointItemId;

    /**
     * 预约总时长 默认取预约项目时长
     */
    @ApiModelProperty(value = "预约总时长 默认取预约项目时长")
    private Integer appointDuration;

    /**
     * 预约日期
     */
    @ApiModelProperty(value = "预约日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date appointStartTime;

    /**
     * 预约结束时间
     */
    @ApiModelProperty(value = "预约结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date appointEndTime;

    /**
     * 预约时间段 预约开始时间-预约结束时间
     */
    @ApiModelProperty(value = "预约时间段 预约开始时间-预约结束时间")
    private String appointPeriod;

    /**
     * 牙位
     */
    @ApiModelProperty(value = "牙位")
    private String toothBit;

    /**
     * 预约内容
     */
    @ApiModelProperty(value = "预约内容")
    private String appointContent;

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

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;

    /**
     * 是否启用 是否有效
     */
    @ApiModelProperty(value = "是否启用 是否有效")
    private Boolean inservice;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date crtTime;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String crtName;

    /** 时长分解列表 */
    @ApiModelProperty(value = "时长分解列表")
    private List<AppointmentSplitVo> splitList;
}
