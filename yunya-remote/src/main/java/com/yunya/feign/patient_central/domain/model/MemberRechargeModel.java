package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
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
    @NotNull(message = "患者id不能为空")
    @ApiModelProperty(value = "患者id",required = true)
    private Integer patientId;

    /**
     * 会员卡号
     */
    @NotNull(message = "会员卡号不能为空")
    @ApiModelProperty(value = "会员卡号",required = true)
    private String memberId;

    /**
     * 充值本金
     */
    @NotNull(message = "充值本金不能为空")
    @ApiModelProperty(value = "充值本金",required = true)
    private BigDecimal rechargePrincipal;

    /**
     * 充值赠金
     */
    @NotNull(message = "充值赠金不能为空")
    @ApiModelProperty(value = "充值赠金",required = false)
    private BigDecimal rechargeBonus;

    /**
     * 入账方式
     */
    @NotNull(message = "入账方式不能为空")
    @ApiModelProperty(value = "入账方式",required = true)
    private AccountedWayModel accountedWayModel;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;

    /**
     * 充值卡卡号
     */
    @ApiModelProperty(value = "充值卡卡号")
    private String rechargeCardNumber;

    /**
     * 充值类型：0普通充值 1充值卡充值
     */
    @ApiModelProperty(value = "充值类型 0普通充值 1充值卡充值")
    private Byte rechargeType;


    /** 充值卡卡券id */
    @ApiModelProperty(value = "充值卡卡券id", required = true)
    @NotNull
    private Integer cardId;
}
