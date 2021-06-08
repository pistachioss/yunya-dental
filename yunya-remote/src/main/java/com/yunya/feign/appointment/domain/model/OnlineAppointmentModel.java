package com.yunya.feign.appointment.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Future;
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
@ApiModel(value = "OnlineAppointmentModel",description = "在线预约申请参数")
@Data
public class OnlineAppointmentModel implements Serializable {

    /**
     * 预约申请ID
     */
    @ApiModelProperty("预约申请ID")
    private Integer id;

    /**
     * 微信唯一标识openId,唯一标识
     */
    @ApiModelProperty(value = "openId,唯一标识",required = true)
    @NotNull(message = "openId不能为空")
    @NotBlank(message = "openId不能为空")
    private String openId;
    /**
     * 门诊ID
     */
    @ApiModelProperty(value = "门诊ID",required = true)
    @NotNull(message = "门诊不能为空")
    private Integer orgId;

    /**
     * 医生ID
     */
    @ApiModelProperty(value = "医生ID",required = true)
    @NotNull(message = "医生不能为空")
    private Integer dentistId;

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
     * 预约日期
     */
    @ApiModelProperty(value = "预约日期",required = true)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @NotNull(message = "预约日期不能为空")
    @Future(message = "预约日期必须是当天之后")
    private Date appointDate;

    /**
     * 预约时间
     */
    @ApiModelProperty(value = "预约时间",required = true)
    @NotNull(message = "预约时间不能为空")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private String appointTime;

    /**
     * 就诊患者名字
     */
    @ApiModelProperty(value = "就诊患者名字",required = true)
    @NotNull(message = "名字不能为null")
    @NotBlank(message = "名字不能为空")
    private String patientName;

    /**
     * 患者手机号
     */
    @ApiModelProperty(value = "患者手机号",required = true)
    @NotNull(message = "手机号不能为null")
    @NotBlank(message = "手机号不能为空")
    private String patientPhone;
}
