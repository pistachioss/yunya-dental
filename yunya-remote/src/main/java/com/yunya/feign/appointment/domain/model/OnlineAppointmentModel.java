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
@ApiModel(value = "OnlineAppointmentVo",description = "在线预约申请参数")
@Data
public class OnlineAppointmentModel implements Serializable {
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
     * 预约内容
     */
    @ApiModelProperty("预约内容")
    private String appointContent;

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
     * 预约时长
     */
    @ApiModelProperty("预约时长")
    private Integer duration;

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
}
