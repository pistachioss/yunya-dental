package com.yunya.feign.treatment_other.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * 简介: 根尖片首页缩影uri 统计
 *
 * @author: zkq
 * @date: 2020/9/1 15:04
 * @description:
 * @since: 1.0.0
 */
@ApiModel("根尖片首页缩影uri 统计")
@Data
@ToString
public class ToothBitDataByNumQuery {
    @ApiModelProperty(value = "患者id")
    @NotNull
    private Integer patientId;
    @ApiModelProperty(value = "组织id")
    @NotNull
    private Integer orgId;
    @ApiModelProperty(value = "就诊id")
    @NotNull
    private Integer treatmentRecordId;
    @ApiModelProperty(value = "牙医id")
    @NotNull
    private Integer dentistId;
}
