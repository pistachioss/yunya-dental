package com.yunya.feign.appointment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 在线预约参数
 * @author: LHB
 * @create: 2021-05-19 10:02
 **/
@ApiModel(value = "OnlineAppointmentVo",description = "在线预约返回参数")
@Data
public class OnlineAppointmentVo implements Serializable {
    /**
     * 预约申请ID (onlineAppointment表中主键ID)
     */
    @ApiModelProperty("预约申请ID")
    private Integer id;

    /**
     * 预约ID (appointment表中主键ID)
     */
    @ApiModelProperty("预约ID,可能为空")
    private Integer appointmentId;

    /**
     * 门诊ID
     */
    @ApiModelProperty("门诊ID")
    private Integer orgId;

    /**
     * 医生ID
     */
    @ApiModelProperty("医生ID")
    private Integer dentistId;

    /**
     * 患者ID,可能为空
     */
    @ApiModelProperty("患者ID,可能为空")
    private Integer patientId;

    /**
     * 预约项目ID
     */
    @ApiModelProperty("预约项目ID")
    private Integer appointItemId;

    /**
     * 预约内容
     */
    @ApiModelProperty("预约内容")
    private String appointContent;

    /**
     * 预约日期
     */
    @ApiModelProperty("预约日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private String appointDate;

    /**
     * 预约时间
     */
    @ApiModelProperty("预约时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private String appointTime;

    /**
     * 预约时长
     */
    @ApiModelProperty("预约时长")
    private Integer duration;

    /**
     * 就诊患者名字
     */
    @ApiModelProperty("就诊患者名字")
    private String patientName;

    /**
     * 患者手机号
     */
    @ApiModelProperty("患者手机号")
    private String patientPhone;

    /**
     * 预约申请状态 0-未确认；1-确认; 2-取消
     */
    @ApiModelProperty("预约状态 0-未确认；1-确认; 2-取消")
    private Byte status;

    /**
     * 是否有效，是否删除(默认有效) 1-有效；0删除
     */
    @ApiModelProperty("是否有效，是否删除(默认有效) 1-有效；0删除")
    private Boolean inservice;

    /**
     * 预约申请时间
     */
    @ApiModelProperty("预约申请时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private String crtTime;

    /**
     * 病历编号
     */
    @ApiModelProperty("病历编号")
    private String medicalNumber;

    /**
     * 预约确认状态
     */
    @ApiModelProperty("预约确认状态")
    private Boolean confirmStatus;
}
