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
@ApiModel("会员消费列表Vo")
public class BaseMemberExpendLogVo {

    /** 操作id **/
    @ApiModelProperty(value = "操作id")
    private Integer occurLogId;

    /** 消费日期 */
    @ApiModelProperty(value = "消费日期")
    private String occurDate;

    /** 账单日期 */
    @ApiModelProperty(value = "账单日期")
    private String orderDate;

    /** 订单号 */
    @ApiModelProperty(value = "订单号")
    private String orderNum;

    /** 患者姓名 */
    @ApiModelProperty(value = "患者姓名")
    private String name;

    /** 手机号 */
    @ApiModelProperty(value = "手机号")
    private String mobile;

    /** 主卡人姓名 */
    @ApiModelProperty(value = "主卡人姓名")
    private String masterCardName;

    /** 会员卡号 */
    @ApiModelProperty(value = "会员卡号")
    private String cardNumber;

    /** 会员级别id */
    @ApiModelProperty(value = "会员级别id")
    private Integer memberLevelId;

    /** 会员卡级别名称 */
    @ApiModelProperty(value = "会员卡级别名称")
    private String memberLevelName;

    /** 消费本金金额 */
    @ApiModelProperty(value = "消费本金金额")
    private BigDecimal principalAmount;

    /** 消费赠金金额 */
    @ApiModelProperty(value = "消费赠金金额")
    private BigDecimal bonusAmount;

    /** 消费后会员卡余额（本金） */
    @ApiModelProperty(value = "消费后会员卡余额（本金）")
    private BigDecimal currentRechargePrincipal;

    /** 消费后会员卡赠金（赠金） */
    @ApiModelProperty(value = "消费后会员卡赠金（赠金）")
    private BigDecimal currentRechargeBonus;

    /** 消费人 */
    @ApiModelProperty(value = "消费人")
    private String operatorUserName;
}