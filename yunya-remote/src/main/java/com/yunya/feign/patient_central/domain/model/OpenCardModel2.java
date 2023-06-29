package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

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
@ApiModel("充值开卡（添加会员卡）")
public class OpenCardModel2 implements Serializable {
    /**
     * 患者ID
     */
    @NotNull(message = "患者ID不能为空")
    @ApiModelProperty(value = "患者ID",required = true)
    private Integer patientId;

    /**
     * 会员卡类型
     */
    @NotNull(message = "会员卡类型不能为空")
    @ApiModelProperty(value = "会员卡类型",required = true)
    private Integer memberTypeId;

    /**
     * 会员卡卡号
     */
    @NotNull
    @ApiModelProperty(value = "会员卡卡号")
    private String cardNumber;

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

}
