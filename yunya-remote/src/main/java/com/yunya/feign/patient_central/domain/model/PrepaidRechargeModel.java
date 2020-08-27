package com.yunya.feign.patient_central.domain.model;

import com.yunya.models.patient_central.PrepaidRechargeTollRecord;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 简单介绍:</br> 预付款充值
 *
 * @author: WY
 * @date 2020/8/22 13:57
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class PrepaidRechargeModel implements Serializable {

    /**
     * 患者id
     */
    @ApiModelProperty(value = "患者id",required = true)
    private Integer patientId;

    /**
     * 预付款卡号ID
     */
    @ApiModelProperty(value = "预付款卡号ID",required = true)
    private String prepaidId;

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
    private PrepaidRechargeTollRecordModel prepaidRechargeTollRecordModel;

    /**
     * 充值卡卡号
     */
    @ApiModelProperty(value = "充值卡卡号")
    private String rechargeCardNumber;

    /**
     * 备注 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;

    /**
     * 充值类型
     */
    @ApiModelProperty(value = "充值类型 0普通充值 1充值卡充值")
    private Byte rechargeType;

}
