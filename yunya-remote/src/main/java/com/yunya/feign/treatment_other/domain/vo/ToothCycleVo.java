package com.yunya.feign.treatment_other.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * 简介: 牙周期列表模型
 *
 * @author: Zkq
 * @description:
 * @since: 1.0.0
 */
@ApiModel("牙周期列表模型")
@Data
@ToString
public class ToothCycleVo {

    @ApiModelProperty(value = "主键id")
    private int id;
    @ApiModelProperty(value = "诊所id")
    private int orgId;
    @ApiModelProperty(value = "患者id")
    private Boolean patientId;
    @ApiModelProperty(value = "就诊id")
    private Boolean treatmentRecordId;
    @ApiModelProperty(value = "牙医id")
    private Boolean dentistId;
    @ApiModelProperty(value = "牙周期")
    private Boolean toothCycle;
    @ApiModelProperty(value = "检查日期")
    private Boolean examinationData;

}
