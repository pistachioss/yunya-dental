package com.yunya.feign.appointment.domain.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 在线预约申请参数
 * @author: LHB
 * @create: 2021-05-19 10:02
 **/
@ApiModel(value = "OnlineAppointmentForm",description = "在线预约申请参数")
@Data
public class OnlineAppointmentForm implements Serializable {
    /**
     * 预约申请ID (onlineAppointment表中主键ID)
     */
    @ApiModelProperty(value = "预约申请ID",required = true)
    @NotNull(message = "预约申请ID不能为空")
    private Integer id;

    /**
     * 门诊ID
     */
    @ApiModelProperty(value = "门诊ID",required = true)
    @NotNull(message = "门诊ID不能为空")
    private Integer orgId;

    /**
     * 医生ID
     */
    @ApiModelProperty(value = "医生ID",required = true)
    @NotNull(message = "医生不能为空")
    private Integer dentistId;

    /**
     * 微信用户唯一标识
     */
    @ApiModelProperty(value = "微信用户唯一标识",required = true)
    @NotBlank(message = "openId不能为空")
    @NotNull(message = "openId不能为空")
    private String openId;

    /**
     * 患者ID,可能为空
     */
    @ApiModelProperty("患者ID,可能为空")
    private Integer patientId;

    /**
     * 预约项目ID
     */
    @ApiModelProperty(value = "预约项目ID",required = true)
    @NotNull(message = "预约项目不能为空")
    private Integer appointItemId;

    /**
     * 预约内容
     */
    @ApiModelProperty("预约内容")
    private String appointContent;

    /**
     * 预约日期
     */
    @ApiModelProperty(value = "预约日期",required = true)
    @NotNull(message = "预约日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date appointDate;

    /**
     * 预约时间
     */
    @ApiModelProperty(value = "预约时间",required = true)
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date appointTime;

    /**
     * 预约时长
     */
    @ApiModelProperty("预约时长")
    private Integer duration;

    /**
     * 就诊患者名字
     */
    @ApiModelProperty(value = "就诊患者名字",required = true)
    @NotNull(message = "预约患者名字不能为null")
    @NotBlank(message = "预约患者名字不能为空")
    private String patientName;

    /**
     * 患者手机号
     */
    @ApiModelProperty(value = "患者手机号",required = true)
    @NotNull(message = "患者手机号不能为null")
    @NotBlank(message = "患者手机号不能为空")
    private String patientPhone;

    /**
     * 预约申请状态 0-申请中；1-通过；2-取消
     */
    @ApiModelProperty("预约申请状态 0-申请中；1-通过；2-取消")
    private Byte status;

    /**
     * 微信唯一标识openId,唯一标识
     */
    @ApiModelProperty(value = "union_id,唯一标识",required = true)
    @NotNull(message = "unionId不能为空")
    @NotBlank(message = "unionId不能为空")
    private String unionId;

    /**
     * 意向预约来源 0公众号 1小程序
     */
    @ApiModelProperty(value = "意向预约来源 0公众号 1小程序",required = true)
    @NotNull(message = "意向预约来源不能为空")
    private Integer wxType;

    /**
     * 预约时间 0上午 1下午
     */
    @ApiModelProperty(value = "预约时间 0上午 1下午",required = true)
    @NotNull(message = "预约时间不能为空")
    private Integer appointMa;
}
