package com.yunya.feign.treatment_other.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * 简介: 牙周期列表模型
 *
 * @author: chow
 * @date: 2020/8/11 15:04
 * @description:
 * @since: 1.0.0
 */
@ApiModel("返回牙周期列表模型")
@Data
@ToString
public class ToothCycleFindDataByIdVo {
    @ApiModelProperty(value = "牙周期")
    private String toothCycle;
}
