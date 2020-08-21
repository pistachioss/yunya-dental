package com.yunya.feign.treatment_other.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 简介: 牙周期列表模型
 *
 * @author: chow
 * @date: 2020/8/11 15:04
 * @description:
 * @since: 1.0.0
 */
@ApiModel("牙周期列表模型")
@Data
@ToString
public class ToothCycleModel {

    @ApiModelProperty(value = "诊所id")
    @NotNull
    private int orgId;
    @ApiModelProperty(value = "患者id")
    @NotNull
    private int patientId;
    @ApiModelProperty(value = "就诊id")
    @NotNull
    private int treatmentRecordId;
    @ApiModelProperty(value = "牙医id")
    @NotNull
    private int dentistId;
    @ApiModelProperty(value = "牙周期")
    private String toothCycle;
    @ApiModelProperty(value = "检查日期")
    @NotNull
    private Date examinationData;
}
