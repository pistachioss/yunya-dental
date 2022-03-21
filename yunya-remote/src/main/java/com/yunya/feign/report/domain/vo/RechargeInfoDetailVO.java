package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介：充值卡充值统计-充值统计详情返回
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/3/21 15:48
 * @since: 1.0.0
 */
@ApiModel(value = "充值卡充值统计-充值统计详情返回")
@Data
@ToString
public class RechargeInfoDetailVO implements Serializable {
    @ApiModelProperty(value = "充值卡名称")
    @Excel(name = "充值卡名称")
    private String couponName;

    @ApiModelProperty(value = "卡号")
    @Excel(name = "卡号")
    private String cardNumber;

    @ApiModelProperty(value = "充值门诊")
    @Excel(name = "充值门诊")
    private String rechargeOrgName;

    @ApiModelProperty(value = "充值时间")
    @Excel(name = "充值时间")
    private String rechargeDate;

    @ApiModelProperty(value = "患者")
    @Excel(name = "患者")
    private String patientName;

    @ApiModelProperty(value = "患者手机号")
    @Excel(name = "患者手机号")
    private String patientMobile;

    @ApiModelProperty(value = "充值预付款账户")
    @Excel(name = "充值预付款账户")
    private String rechargeAccount;

    @ApiModelProperty(value = "充值本金")
    @Excel(name = "充值本金")
    private BigDecimal rechargeAmount;

    @ApiModelProperty(value = "充值赠金")
    @Excel(name = "充值赠金")
    private BigDecimal rechargeBonus;

    @ApiModelProperty(value = "充值人")
    @Excel(name = "充值人")
    private String rechargeUser;
}
