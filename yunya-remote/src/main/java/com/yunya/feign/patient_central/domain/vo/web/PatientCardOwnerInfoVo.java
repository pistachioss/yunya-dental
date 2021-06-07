package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 简介: 已绑定卡主信息
 *
 * @author: WY
 * @date: 2021/5/13 09:15
 * @description:
 * @since: 1.0.0
 */
@ApiModel("已绑定卡主信息")
@Data
public class PatientCardOwnerInfoVo implements Serializable {

    /** 卡主id */
    @ApiModelProperty("卡主id")
    private String masterCardId;

    /** 卡主会员号 */
    @ApiModelProperty("卡主会员号")
    private String cardNumber;

    /** 卡主会员卡类型 */
    @ApiModelProperty("卡主会员卡类型")
    private Integer memberTypeId;

    /** 卡主会员卡类型名称 */
    @ApiModelProperty("卡主会员卡类型名称")
    private String memberTypeName;

    /** 卡主姓名 */
    @ApiModelProperty("卡主姓名")
    private String name;

    /** 是否可使用权益 */
    @ApiModelProperty("是否可使用权益")
    private Boolean isDiscount;

    /** 是否可使用余额 */
    @ApiModelProperty("是否可使用余额")
    private Boolean isMoney = false;
}