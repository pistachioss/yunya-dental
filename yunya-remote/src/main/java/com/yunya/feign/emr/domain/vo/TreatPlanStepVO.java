package com.yunya.feign.emr.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 简介：治疗计划步骤VO
 *
 * @author: chenlin
 * @Description: 治疗计划步骤VO
 * @Date: 2022/1/11 10:28
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("治疗计划步骤VO")
public class TreatPlanStepVO implements Serializable {

    /** 步骤Id*/
    @ApiModelProperty("步骤id")
    private Integer treatPlanStepId;

    /** 计划id*/
    @ApiModelProperty("计划id")
    private Integer planId;

    /** 步骤名称*/
    @ApiModelProperty(value = "步骤名称")
    private String stepName;

    /** 数量*/
    @ApiModelProperty("数量")
    private Integer quanity = 0;

    /** 金额*/
    @ApiModelProperty("金额")
    private BigDecimal amount = new BigDecimal("0.00");

    /** 状态：1-未开始; 2-进行中; 3-全部完成; 4-提前终止*/
    @ApiModelProperty(value = "状态：1-未开始; 2-进行中; 3-全部完成; 4-提前终止")
    private Byte status;

    /** 步骤明细*/
    @ApiModelProperty(value = "步骤明细")
    private List<TreatPlanDetailVO> treatPlanDetails;
}
