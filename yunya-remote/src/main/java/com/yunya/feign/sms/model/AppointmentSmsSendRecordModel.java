package com.yunya.feign.sms.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介：预约提醒短信模型
 *
 * @author: chenlin
 * @Description: 预约提醒短信模型
 * @Date: 2020/12/19 16:42
 * @since: 1.0.0
 */
@ApiModel("预约提醒短信模型")
@ToString
@Data
public class AppointmentSmsSendRecordModel extends SmsCommonSendRecordModel implements Serializable{
    /** 预约id列表不能为空 */
    @ApiModelProperty(value = "预约id", required = true)
    @NotNull(message = "预约id不能为空")
    private Integer appointId;

    /**
     * 预约医生姓名
     */
    @ApiModelProperty(value = "预约医生姓名",required = true)
    @NotBlank
    private String dentistName;

    /**
     * 预约日期
     */
    @ApiModelProperty(value = "预约日期",required = true)
    @NotBlank
    private String appointDate;

    /**
     * 预约时间
     */
    @ApiModelProperty(value = "预约时间",required = true)
    @NotBlank
    private String appointTime;

    /**
     * 性别: 0-男，1-女
     */
    @ApiModelProperty(value = "性别: 0-男，1-女",required = true)
    @NotNull
    private Integer gender;

    /**
     * 年龄
     */
    @ApiModelProperty(value = "年龄",required = true)
    @NotNull
    private Integer age;
}
