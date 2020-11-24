package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 简介: 报表预付款退费vo
 *
 * @author: WY
 * @date: 2020/10/24 14:11
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("预付款退费Vo")
public class BasePrepaidReturnLogVo {

    /** 操作id **/
    @ApiModelProperty("操作id")
    private Integer occurLogId;

    /** 退款日期 */
    @ApiModelProperty("退款日期")
    private String occurDate;

    /** 患者姓名 */
    @ApiModelProperty("患者姓名")
    private String name;

    /** 手机号 */
    @ApiModelProperty("手机号")
    private String mobile;

    /** 会员卡号 */
    @ApiModelProperty("会员卡号")
    private String cardNumber;

    /** 会员级别id */
    @ApiModelProperty("会员级别id")
    private Integer memberLevelId;

    /** 会员卡级别名称 */
    @ApiModelProperty("会员卡级别名称")
    private String memberLevelName;

    /** 退款本金金额 */
    @ApiModelProperty("退款本金金额")
    private BigDecimal principalAmount;

    /** 退款赠金金额 */
    @ApiModelProperty("退款赠金金额")
    private BigDecimal bonusAmount;

    /** 退款后会员卡余额（本金） */
    @ApiModelProperty("退款后会员卡余额（本金）")
    private BigDecimal currentRechargePrincipal;

    /** 退款后会员卡余额（赠金） */
    @ApiModelProperty("退款后会员卡余额（赠金）")
    private BigDecimal currentRechargeBonus;

    /** 入账方式 */
    @ApiModelProperty("入账方式")
    private Integer paymentId;

    /** 入账方式名称 */
    @ApiModelProperty("入账方式名称")
    private String paymentManner;

    /** 退款人 */
    @ApiModelProperty("退款人")
    private String operatorUserName;
}