package com.yunya.feign.emr.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 简介：治疗计划步骤添加模型
 *
 * @author: chenlin
 * @Description: 治疗计划步骤添加模型
 * @Date: 2022/1/11 10:28
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("治疗计划步骤添加模型")
public class TreatPlanStepModel implements Serializable {

    /** 步骤Id*/
    @ApiModelProperty("步骤id")
    private Integer treatPlanStepId;

    /** 步骤名称*/
    @ApiModelProperty(value = "步骤名称", required = true)
    @NotEmpty(message = "步骤名称不能为空")
    private String stepName;

    /** 步骤明细*/
    @ApiModelProperty(value = "步骤明细", required = true)
    @NotNull(message = "步骤明细不能为空")
    @Min(value = 1, message = "步骤明细不能为空")
    private List<TreatPlanDetailModel> treatPlanDetails;

    /** 创建人id*/
    private Integer crtId;

    /** 创建时间*/
    private Date crtTime;
}
