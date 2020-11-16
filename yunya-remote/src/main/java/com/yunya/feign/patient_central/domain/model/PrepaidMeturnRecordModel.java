package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简单介绍:</br> 预付款退费
 *
 * @author: WY
 * @date 2020/8/26 16:31
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class PrepaidMeturnRecordModel implements Serializable {

    /**
     * 预付款卡号
     */
    @ApiModelProperty(value = "预付款卡号",required = true)
    private String prepaidId;

    /**
     * 患者id
     */
    @ApiModelProperty(value = "患者id",required = true)
    private Integer patientId;

    /**
     * 退还本金
     */
    @ApiModelProperty(value = "退还本金",required = false)
    private BigDecimal returnPrincipalAmount;

    /**
     * 退还赠金
     */
    @ApiModelProperty(value = "退还赠金",required = false)
    private BigDecimal returnGiftAmount;

    /**
     * 退费方式ID
     */
    @ApiModelProperty(value = "退费方式ID",required = false)
    private Integer returnWayId;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;

}
