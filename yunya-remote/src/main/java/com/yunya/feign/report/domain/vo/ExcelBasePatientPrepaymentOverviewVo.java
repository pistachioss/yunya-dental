package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 简介: 预付款概况Vo
 *
 * @author: WY
 * @date: 2020/10/24 14:11
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("预付款概况Vo")
public class ExcelBasePatientPrepaymentOverviewVo {

    /** 患者姓名 */
    @Excel(name = "患者姓名")
    @ApiModelProperty(value = "患者姓名")
    private String name;

    /** 手机号 */
    @Excel(name = "手机号")
    @ApiModelProperty(value = "手机号")
    private String mobile;

    /** 会员卡号 */
    @Excel(name = "预付款账户")
    @ApiModelProperty(value = "预付款账户")
    private String cardNumber;

    /** 开卡门诊 */
    @Excel(name = "开户门诊")
    @ApiModelProperty(value = "开户门诊")
    private String abbreviation;

    /** 开卡日期 */
    @Excel(name = "开户日期")
    @ApiModelProperty(value = "开户日期")
    private String cardOpeningDate;

    /** 剩余预付款余额（含赠送金额） */
    @Excel(name = "剩余预付款余额（含赠送金额）")
    @ApiModelProperty(value = "剩余预付款余额（含赠送金额）")
    private BigDecimal principalAmount;

    /** 剩余预付款赠送金额 */
    @Excel(name = "剩余预付款赠送金额")
    @ApiModelProperty(value = "剩余预付款赠送金额")
    private BigDecimal bonusAmount;
    
}