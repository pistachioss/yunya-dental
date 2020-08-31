package com.yunya.feign.patient_central.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br>
 *
 * @author: WY
 * @date 2020/8/14 20:48
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel(value = "会员卡类型修改")
public class CardTypeForm implements Serializable {

    /**
     * 会员卡卡号
     */
    @ApiModelProperty(value = "会员卡卡号")
    private String cardNumber;

    /**
     * 会员卡类型ID
     */
    @ApiModelProperty(value = "会员卡类型ID",required = true)
    private Integer memberTypeId;

    /**
     * 患者id
     */
    @ApiModelProperty(value = "患者id",required = true)
    private Integer patientId;


}
