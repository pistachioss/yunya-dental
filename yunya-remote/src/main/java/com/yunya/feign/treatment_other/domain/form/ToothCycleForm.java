package com.yunya.feign.treatment_other.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * 简介: 牙周期列表模型
 *
 * @author: chow
 * @date: 2020/8/11 15:04
 * @description:
 * @since: 1.0.0
 */
@ApiModel("修改牙周期列表模型")
@Data
@ToString
public class ToothCycleForm {

    @ApiModelProperty(value = "主键id")
    private Integer id;
    @ApiModelProperty(value = "牙周期")
    private String toothCycle;
}
