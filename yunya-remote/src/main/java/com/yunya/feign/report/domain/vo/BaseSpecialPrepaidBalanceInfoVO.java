package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

import static com.yunya.framework.common.annation.Excel.Type.EXPORT;

/**
 * 简介: 专项预付款余额结存数据模型
 *
 * @author: chenlin
 * @date: 2023/09/06 14:11
 * @description: 专项预付款余额结存数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("专项预付款余额结存数据模型")
public class BaseSpecialPrepaidBalanceInfoVO implements Serializable {

    /** 患者姓名 */
    @Excel(name = "患者")
    @ApiModelProperty(value = "患者姓名")
    private String name;

    /** 手机号 */
    @Excel(name = "手机号")
    @ApiModelProperty(value = "手机号")
    private String mobile;

    /** 预付款卡号 */
    @Excel(name = "预付款卡号")
    @ApiModelProperty(value = "预付款卡号")
    private String cardNumber;

    /** 预付款类型 */
    @Excel(name = "预付款类型")
    @ApiModelProperty("预付款类型")
    private String type;

    /** 期初本金余额 */
    @Excel(name = "期初本金余额", cellType = Excel.ColumnType.NUMERIC, isStatistics = true, type = EXPORT)
    @ApiModelProperty(value = "期初本金余额")
    private BigDecimal earlyCurrentRechargePrincipal;

    /** 期初赠金余额 */
    @Excel(name = "期初赠金余额", cellType = Excel.ColumnType.NUMERIC, isStatistics = true, type = EXPORT)
    @ApiModelProperty(value = "期初赠金余额")
    private BigDecimal earlyCurrentRechargeBonus;

    /** 本期充值本金 */
    @Excel(name = "本期充值本金", cellType = Excel.ColumnType.NUMERIC, isStatistics = true, type = EXPORT)
    @ApiModelProperty(value = "本期充值本金")
    private BigDecimal thisRechargePrincipalAmount;

    /** 本期充值赠金 */
    @Excel(name = "本期充值赠金", cellType = Excel.ColumnType.NUMERIC, isStatistics = true, type = EXPORT)
    @ApiModelProperty(value = "本期充值赠金")
    private BigDecimal thisRechargeBonusAmount;

    /** 本期消费本金 */
    @Excel(name = "本期消费本金", cellType = Excel.ColumnType.NUMERIC, isStatistics = true, type = EXPORT)
    @ApiModelProperty(value = "本期消费本金")
    private BigDecimal thisExpendPrincipalAmount;

    /** 本期消费赠金 */
    @Excel(name = "本期消费赠金", cellType = Excel.ColumnType.NUMERIC, isStatistics = true, type = EXPORT)
    @ApiModelProperty(value = "本期消费赠金")
    private BigDecimal thisExpendBonusAmount;

    /** 本期退费本金 */
    @Excel(name = "本期退费本金", cellType = Excel.ColumnType.NUMERIC, isStatistics = true, type = EXPORT)
    @ApiModelProperty(value = "本期退费本金")
    private BigDecimal thisReturnPrincipalAmount;

    /** 本期退费赠金 */
    @Excel(name = "本期退费赠金", cellType = Excel.ColumnType.NUMERIC, isStatistics = true, type = EXPORT)
    @ApiModelProperty(value = "本期退费赠金")
    private BigDecimal thisReturnBonusAmount;

    /** 期末本金余额 */
    @Excel(name = "期末本金余额", cellType = Excel.ColumnType.NUMERIC, isStatistics = true, type = EXPORT)
    @ApiModelProperty(value = "期末本金余额")
    private BigDecimal currentRechargePrincipal;

    /** 期末赠金余额 */
    @Excel(name = "期末赠金余额", cellType = Excel.ColumnType.NUMERIC, isStatistics = true, type = EXPORT)
    @ApiModelProperty(value = "期末赠金余额")
    private BigDecimal currentRechargeBonus;
}