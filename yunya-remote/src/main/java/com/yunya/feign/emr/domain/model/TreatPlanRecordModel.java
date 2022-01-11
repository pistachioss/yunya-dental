package com.yunya.feign.emr.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 简介：治疗计划添加模型
 *
 * @author: chenlin
 * @Description: 治疗计划添加模型
 * @Date: 2022/1/11 10:07
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("治疗计划添加模型")
public class TreatPlanRecordModel implements Serializable {
    /** 治疗计划Id*/
    @ApiModelProperty("治疗计划id")
    private Integer treatPlanId;

    /** 门诊id*/
    @ApiModelProperty("门诊id")
    private Integer orgId;

    /** 普通电子病历id*/
    @ApiModelProperty("普通电子病历id")
    private Integer medicalRecordId;

    /** 治疗计划名称*/
    @ApiModelProperty(value = "治疗计划名称", required = true)
    @NotEmpty(message = "治疗计划名称不能为空")
    private String planName;

    /** 概述*/
    @ApiModelProperty(value = "概述")
    private String summary;

    /** 医生id*/
    @ApiModelProperty(value = "医生id", required = true)
    @NotNull(message = "医生id不能为空")
    private Integer dentistId;

    /** 患者id*/
    @ApiModelProperty(value = "患者id", required = true)
    @NotNull(message = "患者id不能为空")
    private Integer patientId;

    /** 备注*/
    @ApiModelProperty(value = "备注")
    private String remark;

    /** 患者是否确认*/
    @ApiModelProperty(value = "患者是否确认")
    private Boolean patientIsConfirm;

    /** 步骤列表*/
    @ApiModelProperty(value = "步骤列表")
    private List<TreatPlanStepModel> treatPlanSteps;
}
