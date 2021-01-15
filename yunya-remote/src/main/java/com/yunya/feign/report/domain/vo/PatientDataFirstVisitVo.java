package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 简介: 患者初诊信息
 *
 * @author: WY
 * @date: 2020/12/16 17:02
 * @description:
 * @since: 1.0.0
 */
@ApiModel(value = "PatientDataFirstVisitVo",description = "患者初诊信息")
@Data
@ToString
public class PatientDataFirstVisitVo implements Serializable {
    /**
     * 患者ID
     */
    @ApiModelProperty("患者ID")
    private Integer patientId;

    /**
     * 初诊日期
     */
    @ApiModelProperty("初诊日期")
    private String firstVisitDate;

    /**
     * 初诊医生
     */
    @ApiModelProperty("初诊医生")
    private String firstVisitDoctors;

    /**
     * 初诊门诊
     */
    @ApiModelProperty("初诊门诊")
    private String firstVisitOutpatient;

    /**
     * 累计消费
     */
    @ApiModelProperty("累计消费")
    private BigDecimal cumulativeConsumption;

}