package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 简介: 报表预付款充值vo
 *
 * @author: WY
 * @date: 2020/10/24 14:11
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("预付款充值列表Vo")
public class BasePrepaidRechargeLogVo {

    /** 操作id **/
    @ApiModelProperty("操作id")
    private Integer occurLogId;

    /** 充值日期 */
    @ApiModelProperty("充值日期")
    private String occurDate;

    /** 患者姓名 */
    @ApiModelProperty("患者姓名")
    private String name;

    /** 手机号 */
    @ApiModelProperty("手机号")
    private String mobile;

    /** 预付款卡号 */
    @ApiModelProperty("预付款卡号")
    private String cardNumber;

    /** 充值本金金额 */
    @ApiModelProperty("充值本金金额")
    private BigDecimal principalAmount;

    /** 充值赠金金额 */
    @ApiModelProperty("充值赠金金额")
    private BigDecimal bonusAmount;

    /** 充值后会员卡余额（本金） */
    @ApiModelProperty("充值后会员卡余额（本金）")
    private BigDecimal currentRechargePrincipal;

    /** 充值后会员卡余额（赠金） */
    @ApiModelProperty("充值后会员卡余额（赠金）")
    private BigDecimal currentRechargeBonus;

    /** 充值方式 */
    @ApiModelProperty("充值方式")
    private String rechargeMethod;

    /** 入账方式 */
    @ApiModelProperty("入账方式")
    private Integer paymentId;

    /** 入账方式名称 */
    @ApiModelProperty("入账方式名称")
    private String paymentManner;

    /** 充值卡号 */
    @ApiModelProperty("充值卡号")
    private String rechargeCardNumber;

    /** 充值人 */
    @ApiModelProperty("充值人")
    private String operatorUserName;

    /** 备注 */
    @ApiModelProperty("备注")
    private String remarks;


}