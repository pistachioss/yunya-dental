package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 简介: 报表会员卡退费vo
 *
 * @author: WY
 * @date: 2020/10/24 14:11
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("会员退费列表Vo")
public class    BaseMemberReturnLogVo {

    /** 操作id **/
    @ApiModelProperty(value = "操作id")
    private Integer occurLogId;

    /** 退款日期 */
    @Excel(name = "退款日期")
    @ApiModelProperty(value = "退款日期")
    private String occurDate;

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

    /** 会员级别id */
    @ApiModelProperty(value = "会员级别id")
    private Integer memberLevelId;

    /** 会员卡名称 */
    @Excel(name = "会员卡名称")
    @ApiModelProperty(value = "会员卡名称")
    private String memberLevelName;

    /** 退本金金额 */
    @Excel(name = "退本金金额")
    @ApiModelProperty(value = "退本金金额")
    private BigDecimal principalAmount = new BigDecimal(0);

    /** 退赠金金额 */
    @Excel(name = "退赠金金额")
    @ApiModelProperty(value = "退赠金金额")
    private BigDecimal bonusAmount = new BigDecimal(0);

    /** 退费后会员卡余额（含赠送金额） */
    @Excel(name = "退费后会员卡余额（含赠送金额）")
    @ApiModelProperty(value = "退费后会员卡余额（含赠送金额）")
    private BigDecimal currentReturnPrincipal = new BigDecimal(0);

    /** 剩余会员卡余额（含赠送金额） */
    @Excel(name = "剩余会员卡余额（含赠送金额）")
    @ApiModelProperty(value = "剩余会员卡余额（含赠送金额）")
    private BigDecimal currentAmount = new BigDecimal(0);

    /** 退费方式字典id */
    @ApiModelProperty(value = "退费方式字典id")
    private Integer paymentId;

    /** 退费人 */
    @Excel(name = "退费方式")
    @ApiModelProperty(value = "退费方式")
    private String paymentManner;

    /** 退款人 */
    @Excel(name = "退款人")
    @ApiModelProperty(value = "退款人")
    private String operatorUserName;

    /** 退费原因*/
    @Excel(name = "退费原因")
    @ApiModelProperty(value = "退费原因")
    private String remarks;
}