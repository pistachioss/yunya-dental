package com.yunya.feign.appointment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 患者档案中预约列表视图模型
 * @author: LHB
 * @create: 2020-09-25 18:50
 **/
@Data
@ApiModel(value = "AppointPatientRecordVo", description = "患者档案中预约列表视图模型")
public class AppointPatientRecordVo implements Serializable {
    /**
     * 患者预约ID
     */
    @ApiModelProperty(value = "患者预约ID")
    private Integer id;

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
     * 诊所名称
     */
    @ApiModelProperty(value = "诊所名称")
    private String orgName;

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
     * 门诊设备编号
     */
    @ApiModelProperty(value = "门诊设备编号")
    private String clinicDeviceItemNumber;

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
     * 预约内容
     */
    @ApiModelProperty(value = "预约内容")
    private String appointContent;

    /**
     * 预约总时长 默认取预约项目时长
     */
    @ApiModelProperty(value = "预约总时长 默认取预约项目时长")
    private Integer appointDuration;

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

}
