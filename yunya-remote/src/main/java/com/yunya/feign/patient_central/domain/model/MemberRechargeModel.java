package com.yunya.feign.patient_central.domain.model;

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
public class MemberRechargeModel implements Serializable {

    /**
     * 会员卡ID
     */
    @ApiModelProperty(value = "会员卡ID",required = true)
    private Integer memberId;

    /**
     * 充值本金
     */
    @ApiModelProperty(value = "充值本金",required = true)
    private BigDecimal rechargePrincipal;

    /**
     * 充值赠金
     */
    @ApiModelProperty(value = "充值赠金",required = true)
    private BigDecimal rechargeBonus;

    /**
     * 入账方式
     */
    @ApiModelProperty(value = "入账方式",required = true)
    private List<AccountedWayModel> accountedWayModelList;


}
