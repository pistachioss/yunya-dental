package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：本月初诊且复诊VO
 *
 * @author: chenlin
 * @Description: 本月初诊且复诊VO
 * @Date: 2021/12/4 15:30
 * @since: 1.0.0
 */
@ApiModel("本月初诊且复诊VO")
@Data
@ToString
public class InMonthReFirstVisitVO implements Serializable {

    /** 门诊ID*/
    @ApiModelProperty("门诊ID")
    private Integer orgId;
    /** 医生ID*/
    @ApiModelProperty("医生ID")
    private Integer dentistId;
    /** 本月初诊且复诊数量*/
    @ApiModelProperty("本月初诊且复诊VO数量")
    private Integer count;
}
