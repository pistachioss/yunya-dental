package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介: 患者资料VO
 *
 * @author: WY
 * @date: 2020/12/16 17:02
 * @description:
 * @since: 1.0.0
 */
@ApiModel(value = "PatientDataVo",description = "患者资料VO")
@Data
@ToString
public class PatientDataVo implements Serializable {
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
    private Integer cumulativeConsumption;

    /**
     * 末诊日期
     */
    @ApiModelProperty("末诊日期")
    private String lastVisitDate;

    /**
     * 末诊医生
     */
    @ApiModelProperty("末诊医生")
    private String lastVisitDoctors;

    /**
     * 末诊门诊
     */
    @ApiModelProperty("末诊门诊")
    private String lastVisitOutpatient;

    /**
     * 欠费总额
     */
    @ApiModelProperty("欠费总额")
    private Integer totalArrears;

    /**
     * 预约次数
     */
    @ApiModelProperty("预约次数")
    private Integer totalReservation;

    /**
     * 履约次数
     */
    @ApiModelProperty("履约次数")
    private Integer totalPerformance;

    /**
     * 失约次数
     */
    @ApiModelProperty("失约次数")
    private Integer totalMissedAppointment;

    /**
     * 就诊次数
     */
    @ApiModelProperty("就诊次数")
    private Integer numberOfVisits;


}