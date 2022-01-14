package com.yunya.feign.emr.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 简介：治疗计划记录VO
 *
 * @author: chenlin
 * @Description: 治疗计划记录VO
 * @Date: 2022/1/12 13:51
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("治疗计划记录VO")
public class TreatPlanSimpleVO implements Serializable {

    /** 治疗计划id*/
    @ApiModelProperty("治疗计划id")
    private Integer planId;

    /** 治疗计划名称*/
    @ApiModelProperty("治疗计划名称")
    private String planName;

    /** 医生id*/
    @ApiModelProperty(value = "医生id")
    private Integer dentistId;

    /** 医生*/
    @ApiModelProperty(value = "医生")
    private String dentistName;

    /** 总数量*/
    @ApiModelProperty("总数量")
    private Integer totalQuanity = 0;

    /** 总金额*/
    @ApiModelProperty("总金额")
    private BigDecimal totalAmount = new BigDecimal("0.00");

    /** 步骤列表*/
    @ApiModelProperty(value = "步骤列表")
    private List<TreatPlanStepVO> treatPlanSteps;
}
