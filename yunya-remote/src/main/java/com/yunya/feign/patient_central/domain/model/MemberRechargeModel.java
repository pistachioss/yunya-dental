package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 简单介绍:</br> 会员卡充值Model
 *
 * @author: WY
 * @date 2020/8/15 15:41
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("会员卡充值Model")
public class MemberRechargeModel implements Serializable {

    /**
     * 患者id
     */
    @ApiModelProperty(value = "患者id",required = true)
    private Integer patientId;

    /**
     * 会员卡号
     */
    @ApiModelProperty(value = "会员卡号",required = true)
    private String memberId;

    /**
     * 充值本金
     */
    @ApiModelProperty(value = "充值本金",required = true)
    private BigDecimal rechargePrincipal;

    /**
     * 充值赠金
     */
    @ApiModelProperty(value = "充值赠金",required = false)
    private BigDecimal rechargeBonus;

    /**
     * 入账方式
     */
    @ApiModelProperty(value = "入账方式",required = true)
    private AccountedWayModel accountedWayModel;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;


}
