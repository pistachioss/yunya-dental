package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介：门诊业绩VO
 *
 * @author: chenlin
 * @Description:
 * @Date: 2021/4/06 10:07
 * @since: 1.0.0
 */
@ApiModel("门诊业绩VO")
@Data
@ToString
public class ClinicPerformanceVO implements Serializable {

    /** 门诊*/
    @Excel(name = "门诊")
    @ApiModelProperty("门诊")
    private String abbreviation;

    /** 月份*/
    @Excel(name = "月份")
    @ApiModelProperty("月份")
    private String month;

    /** 实际值*/
    @Excel(name = "实际值")
    @ApiModelProperty("实际值")
    private BigDecimal workload;

    /** 目标值*/
    @Excel(name = "目标值")
    @ApiModelProperty("目标值")
    private BigDecimal goal;

    /** 完成度*/
    @Excel(name = "完成度")
    @ApiModelProperty("完成度")
    private String completePercentage;
}
