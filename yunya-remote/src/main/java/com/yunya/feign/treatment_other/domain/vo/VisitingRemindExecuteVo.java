package com.yunya.feign.treatment_other.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 执行提醒VO
 *
 * @description: 执行提醒VO
 * @author: chenl
 * @create: 2021-04-28
 **/
@ApiModel(value = "执行提醒VO")
@Data
@ToString
public class VisitingRemindExecuteVo implements Serializable {

    /** 提醒时间*/
    @Excel(name = "提醒时间", dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "提醒时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date remindDate;

    /** 提醒对象 */
    @Excel(name = "提醒对象")
    @ApiModelProperty(value = "提醒对象")
    private String patientName;

    /** 医生 */
    @Excel(name = "医生")
    @ApiModelProperty(value = "医生")
    private String dentistName;

    /** 提醒内容 */
    @Excel(name = "提醒内容")
    @ApiModelProperty(value = "提醒内容")
    private String remindContent;

    /** 预约时间*/
    @Excel(name = "预约时间")
    @ApiModelProperty(value = "预约时间")
    private String appointDate;

    /** 后续跟踪*/
    @Excel(name = "后续跟踪")
    @ApiModelProperty(value = "后续跟踪")
    private String followUp;
}
