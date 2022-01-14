package com.yunya.feign.emr.domain.form;

import com.yunya.feign.emr.domain.model.TreatPlanDetailModel;
import com.yunya.feign.emr.domain.model.TreatPlanStepModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 简介：治疗计划变更修改模型
 *
 * @author: chenlin
 * @Description: 治疗计划变更修改模型
 * @Date: 2022/1/13 10:12
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("治疗计划变更修改模型")
public class TreatPlanRecordChangeForm implements Serializable {
    /** 治疗计划id*/
    @ApiModelProperty(value = "治疗计划id", required = true)
    @NotNull(message = "治疗计划id")
    private Integer planId;

    /** 变更方式：0-方案确认; 1-方案变更; 2-提前终止; 3-撤销终止*/
    @ApiModelProperty(value = "变更方式：0-方案确认; 1-方案变更; 2-提前终止; 3-撤销终止", required = true)
    @NotNull(message = "变更方式不能为空")
    private Byte changeType;

    /** 方案变更或提前终止或撤销终止的原因*/
    @ApiModelProperty(value = "方案变更或提前终止或撤销终止的原因")
    private String operationReason;

    /** 治疗计划步骤列表*/
    @ApiModelProperty("治疗计划步骤列表")
    private List<TreatPlanStepModel> details;
}
