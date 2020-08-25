package com.yunya.feign.treatment_other.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 新增随访提醒
 * @author: LHB
 * @create: 2020-08-24 19:54
 **/
@ApiModel(value = "新增随访提醒")
@Data
@ToString
public class VisitingRemindModel implements Serializable {

    /**
     * 诊所ID
     */
    @ApiModelProperty(value = "诊所ID", required = true)
    @NotNull(message = "诊所ID不能为空！")
    private Integer orgId;

    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID", required = true)
    @NotNull(message = "患者ID不能为空！")
    private Integer patientId;

    /**
     * 医生ID 默认末诊医生
     */
    @ApiModelProperty(value = "医生ID 默认末诊医生", required = true)
    @NotNull(message = "医生ID不能为空！")
    private Integer dentistId;

    /**
     * 患者就诊ID
     */
    @ApiModelProperty(value = "患者就诊ID", required = true)
    @NotNull(message = "患者就诊ID不能为空！")
    private Integer treatmentId;

    /**
     * 提醒日期
     */
    @ApiModelProperty(value = "提醒日期", required = true)
    @NotNull(message = "提醒日期不能为空！")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date remindDate;

    /**
     * 提醒时间
     */
    @ApiModelProperty(value = "提醒时间", required = true)
    @NotNull(message = "提醒时间不能为空！")
    private String remindTime;

    /**
     * 提醒内容
     */
    @ApiModelProperty(value = "提醒内容", required = true)
    @NotNull(message = "提醒内容不能为空！")
    private String remindContent;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;

}
