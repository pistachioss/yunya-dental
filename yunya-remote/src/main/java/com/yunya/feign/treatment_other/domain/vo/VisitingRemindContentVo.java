package com.yunya.feign.treatment_other.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 随访提醒内容
 * @author: LHB
 * @create: 2020-08-24 20:28
 **/
@ApiModel(value = "随访提醒内容")
@Data
@ToString
public class VisitingRemindContentVo implements Serializable {
    /** 主键 */
    @ApiModelProperty(value = "主键")
    private Integer id;
    /** 患者id */
    @ApiModelProperty(value = "患者id")
    private String patientId;

    /** 患者姓名 */
    @ApiModelProperty(value = "患者姓名")
    private String patientName;

    /** 患者手机号 */
    @ApiModelProperty(value = "患者手机号")
    private String mobile;

    /** 医生id */
    @ApiModelProperty(value = "医生id")
    private String dentistId;

    /**
     * 提醒日期
     */
    @ApiModelProperty(value = "提醒日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date remindDate;

    /** 提醒时间 */
    @ApiModelProperty(value = "提醒时间 HH:mm")
    private String remindTime;

    /**
     * 提醒内容
     */
    @ApiModelProperty(value = "随访内容")
    private String remindContent;

    /** 备注 */
    @ApiModelProperty(value = "备注")
    private String remarks;

    /**
     * 是否启用 0-不启用；1-启用
     */
    @ApiModelProperty(value = "是否启用 0-不启用；1-启用")
    private Boolean inservice;

}
