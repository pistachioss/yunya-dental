package com.yunya.feign.treatment_other.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * 简介: 牙周期列表模型
 *
 * @author: chow
 * @date: 2020/8/11 15:04
 * @description:
 * @since: 1.0.0
 */
@ApiModel("查询牙周期列表模型")
@Data
@ToString
public class ToothBitDataQuery {

    @ApiModelProperty(value = "患者id")
    @NotNull
    private int patientId;

    @ApiModelProperty(value = "牙位id")
    @NotNull
    private Integer toothBit;





}