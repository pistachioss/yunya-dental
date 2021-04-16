package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
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
@ApiModel("会员卡/预付款概况Vo")
public class BasePatientMemberOverviewVo {

    /** 患者姓名 */
    @Excel(name = "患者姓名")
    @ApiModelProperty(value = "患者姓名")
    private String name;

    /** 手机号 */
    @Excel(name = "手机号")
    @ApiModelProperty(value = "手机号")
    private String mobile;

    /** 会员卡号 */
    @Excel(name = "会员卡号")
    @ApiModelProperty(value = "会员卡号")
    private String cardNumber;

    /** 开卡门诊 */
    @Excel(name = "开卡门诊")
    @ApiModelProperty(value = "开卡门诊")
    private String abbreviation;

    /** 开卡日期 */
    @Excel(name = "开卡日期")
    @ApiModelProperty(value = "开卡日期")
    private String cardOpeningDate;

    /** 会员卡余额（本金） */
    @Excel(name = "会员卡余额（本金）", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "会员卡余额（本金）")
    private BigDecimal principalAmount;

    /** 会员卡余额（赠金） */
    @Excel(name = "会员卡余额（赠金）", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "会员卡余额（赠金）")
    private BigDecimal bonusAmount;
    
}