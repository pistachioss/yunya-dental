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

    private Boolean treatmentRecordId;
    @ApiModelProperty(value = "牙医id")
    private Boolean dentistId;
    @ApiModelProperty(value = "牙周期")
    private Boolean toothCycle;
    @ApiModelProperty(value = "检查日期")
    private Boolean examinationData;

}
