package com.yunya.feign.appointment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介：取消预约信息VO
 *
 * @author: chenlin
 * @Description:
 * @Date: 2021/4/27 15:31
 * @since: 1.0.0
 */
@ApiModel("取消预约信息VO")
@ToString
@Data
public class CancelAppointmentVO implements Serializable {

    /** 门诊 */
    @Excel(name = "门诊")
    @ApiModelProperty("门诊")
    private String abbreviation;

    /** 门诊ID*/
    @ApiModelProperty("门诊ID")
    private Integer orgId;

    /** 预约时间*/
    @Excel(name = "预约时间")
    @ApiModelProperty("预约时间")
    private String appointDateTime;

    /** 预约时长*/
    @ApiModelProperty("预约时长")
    private Integer appointDuration;

    /** 初复诊：0-初诊预约；1-复诊预约*/
    @Excel(name = "初复诊", readConverterExp = "0=初诊,1=复诊")
    @ApiModelProperty("初复诊：0-初诊预约；1-复诊预约")
    private Byte appointType;

    /** 预约医生ID*/
    @ApiModelProperty("预约医生ID")
    private Integer dentistId;

    /** 预约医生*/
    @Excel(name = "预约医生")
    @ApiModelProperty("预约医生")
    private String dentistName;

    /** 患者*/
    @Excel(name = "患者")
    @ApiModelProperty("患者")
    private String patientName;

    /** 患者ID*/
    @ApiModelProperty("患者ID")
    private Integer patientId;

    /** 预约状态： 0-预约未到，1-履约，2-取消预约，3-失约*/
    @Excel(name = "预约状态", readConverterExp = "0=预约未到,1=履约,2=取消预约,3=失约")
    @ApiModelProperty("预约状态： 0-预约未到，1-履约，2-取消预约，3-失约")
    private Byte appointStatus;

    /** 取消原因*/
    @Excel(name = "取消原因")
    @ApiModelProperty("取消原因")
    private String cancelReason;

    /** 取消时间*/
    @Excel(name = "取消时间", dateFormat = "yyyy-MM-dd HH:mm")
    @ApiModelProperty("取消时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date cancelDateTime;

    /** 操作人*/
    @Excel(name = "操作人")
    @ApiModelProperty("操作人")
    private String operator;
}
