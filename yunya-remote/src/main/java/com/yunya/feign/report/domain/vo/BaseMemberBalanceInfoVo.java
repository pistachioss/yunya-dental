package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.ExcludeDefaultListeners;
import java.math.BigDecimal;

/**
 * 简介: 会员余额结存表返回模型
 *
 * @author: WY
 * @date: 2020/10/24 14:11
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("会员余额结存表Vo")
public class BaseMemberBalanceInfoVo {

    /** 患者姓名 */
    @ApiModelProperty(value = "患者姓名")
    private String name;

    /** 手机号 */
    @ApiModelProperty(value = "手机号")
    private String mobile;

    /** 预付款类型 */
    @Excel(name = "预付款类型")
    @ApiModelProperty("预付款类型(１：预付款（普通），2-正畸预付款，3-美白预付款)")
    private String type;

    /** 会员卡号 */
    @ApiModelProperty(value = "会员卡号")
    private String cardNumber;

    /** 会员级别id */
    @ApiModelProperty(value = "会员级别id")
    private Integer memberLevelId;

    /** 会员卡级别名称 */
    @Excel(name = "患者姓名")
    @ApiModelProperty(value = "会员卡级别名称")
    private String memberLevelName;

    /** 期初本金余额 */
    @ApiModelProperty(value = "期初本金余额")
    private BigDecimal earlyCurrentRechargePrincipal;

    /** 期初赠金余额 */
    @ApiModelProperty(value = "期初赠金余额")
    private BigDecimal earlyCurrentRechargeBonus;

    /** 本期充值本金 */
    @ApiModelProperty(value = "本期充值本金")
    private BigDecimal thisRechargePrincipalAmount;

    /** 本期充值赠金 */
    @ApiModelProperty(value = "本期充值赠金")
    private BigDecimal thisRechargeBonusAmount;

    /** 本期消费本金 */
    @ApiModelProperty(value = "本期消费本金")
    private BigDecimal thisExpendPrincipalAmount;

    /** 本期消费赠金 */
    @ApiModelProperty(value = "本期消费赠金")
    private BigDecimal thisExpendBonusAmount;

    /** 本期退费本金 */
    @ApiModelProperty(value = "本期退费本金")
    private BigDecimal thisReturnPrincipalAmount;

    /** 本期退费赠金 */
    @ApiModelProperty(value = "本期退费赠金")
    private BigDecimal thisReturnBonusAmount;

    /** 期末本金余额 */
    @ApiModelProperty(value = "期末本金余额")
    private BigDecimal currentRechargePrincipal;

    /** 期末赠金余额 */
    @ApiModelProperty(value = "期末赠金余额")
    private BigDecimal currentRechargeBonus;

    /** 本期转出赠金（亲密付）20 */
    @ApiModelProperty(value = "本期转出赠金（亲密付）")
    private BigDecimal thisRelationBonusOutAmount;

    /** 本期转入赠金（亲密付）21 */
    @ApiModelProperty(value = "本期转入赠金（亲密付）")
    private BigDecimal thisRelationBonusInAmount;
}