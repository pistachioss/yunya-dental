package com.yunya.feign.emr.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
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

    /** 就诊门诊id*/
    @ApiModelProperty("就诊门诊id")
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
    @ApiModelProperty(value = "备注，如果为空请勿传null")
    private String remark;

    /** 方案变更或提前终止或撤销终止的原因*/
    @ApiModelProperty(value = "方案变更或提前终止或撤销终止的原因，如果为空请勿传null")
    private String operationReason;

    /** 状态：0-未确认; 1-已确认; 2-进行中; 3-全部完成; 4-提前终止*/
    @ApiModelProperty("状态：0-未确认; 1-已确认; 2-进行中; 3-全部完成; 4-提前终止")
    private Byte status = 0;

    /** 步骤列表*/
    @ApiModelProperty(value = "步骤列表", required = true)
    @NotNull(message = "步骤列表不能为空")
    @Min(message = "步骤列表不能为空", value = 1)
    private List<TreatPlanStepModel> treatPlanSteps;

    /** 创建人id*/
    private Integer crtId;
}
