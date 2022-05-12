package com.yunya.feign.emr.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：治疗计划类型统计VO
 *
 * @author: chenlin
 * @Description: 治疗计划类型统计VO
 * @Date: 2022/4/24 9:45
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("治疗计划类型统计VO")
public class TreatPlanTypeStatisticsVO implements Serializable {

    /** 门诊*/
    @ApiModelProperty("门诊")
    private String abbreviation;

    /** 治疗计划数量*/
    @ApiModelProperty("治疗计划数量")
    private Integer planNum;

    /** 治疗计划类型*/
    @ApiModelProperty("治疗计划类型")
    private String planTypeName;

    /** 门诊id */
    @ApiModelProperty("门诊id")
    private Integer orgId;

    /** 医生id */
    @ApiModelProperty("医生id")
    private Integer dentistId;

    /** 医生 */
    @ApiModelProperty("医生")
    private String dentistName;

    /** 治疗计划类型id */
    @ApiModelProperty("治疗计划类型id")
    private Integer planTypeId;
}
