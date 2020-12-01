package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 简介: 报表会员卡充值vo
 *
 * @author: WY
 * @date: 2020/10/24 14:11
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("会员充值列表Vo")
public class BaseMemberRechargeLogVo {

    /** 操作id **/
    @ApiModelProperty(value = "操作id")
    private Integer occurLogId;

    /** 充值日期 */
    @ApiModelProperty(value = "充值日期")
    private String occurDate;

    /** 患者姓名 */
    @ApiModelProperty(value = "患者姓名")
    private String name;

    /** 手机号 */
    @ApiModelProperty(value = "手机号")
    private String mobile;

    /** 会员卡号 */
    @ApiModelProperty(value = "会员卡号")
    private String cardNumber;

    /** 会员级别id */
    @ApiModelProperty(value = "会员级别id")
    private Integer memberLevelId;

    /** 会员卡级别名称 */
    @ApiModelProperty(value = "会员卡级别名称")
    private String memberLevelName;

    /** 充值本金金额 */
    @ApiModelProperty(value = "充值本金金额")
    private BigDecimal principalAmount;

    /** 充值赠金金额 */
    @ApiModelProperty(value = "充值赠金金额")
    private BigDecimal bonusAmount;

    /** 充值后会员卡余额（本金） */
    @ApiModelProperty(value = "充值后会员卡余额（含赠送金额）")
    private BigDecimal currentRechargePrincipal;

    /** 剩余会员卡余额（含赠送金额） */
    @ApiModelProperty(value = "剩余会员卡余额（含赠送金额）")
    private BigDecimal currentAmount;

    /** 入账方式 */
    @ApiModelProperty(value = "入账方式")
    private Integer paymentId;

    /** 入账方式名称 */
    @ApiModelProperty(value = "入账方式名称")
    private String paymentManner;

    /** 充值人 */
    @ApiModelProperty(value = "充值人")
    private String operatorUserName;

    /** 备注 */
    @ApiModelProperty(value = "备注")
    private String remarks;
}