package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> 开卡Model
 *
 * @author: WY
 * @date 2020/8/14 13:55
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("开卡（添加会员卡）")
public class OpenCardModel implements Serializable {
    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID",required = true)
    private Integer patientId;

    /**
     * 会员卡类型
     */
    @ApiModelProperty(value = "会员卡类型",required = true)
    private Integer memberTypeId;

}
