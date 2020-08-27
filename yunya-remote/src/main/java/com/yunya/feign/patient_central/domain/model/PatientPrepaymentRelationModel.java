package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br>
 *
 * @author: WY
 * @date 2020/8/27 14:56
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class PatientPrepaymentRelationModel implements Serializable {

    @ApiModelProperty("当前患者id")
    private Integer masterCardId;
    @ApiModelProperty("副卡人id")
    private Integer secondaryCardId;
}
