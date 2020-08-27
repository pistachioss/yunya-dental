package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简单介绍:</br> 会员卡退费Model
 *
 * @author: WY
 * @date 2020/8/26 14:44
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class MemberReturnRecordModel implements Serializable {

    /**
     * 会员卡id
     */
    @ApiModelProperty(value = "会员卡id",required = true)
    private String memberId;

    /**
     * 患者id
     */
    @ApiModelProperty(value = "患者id",required = true)
    private Integer patientId;

    /**
     * 退还本金
     */
    @ApiModelProperty(value = "退还本金",required = true)
    private BigDecimal returnPrincipalAmount;

    /**
     * 退还赠金
     */
    @ApiModelProperty(value = "退还赠金",required = true)
    private BigDecimal returnGiftAmount;

    /**
     * 退费方式ID
     */
    @ApiModelProperty(value = "退费方式ID",required = true)
    private String returnWayId;

    /**
     * 退费原因
     */
    @ApiModelProperty(value = "退费原因")
    private String returnReason;



}
