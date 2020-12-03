package com.yunya.feign.report.domain.vo;

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
    @ApiModelProperty(value = "退款日期")
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

    /** 退款本金金额 */
    @ApiModelProperty(value = "退款本金金额")
    private BigDecimal principalAmount = new BigDecimal(0);

    /** 退款赠金金额 */
    @ApiModelProperty(value = "退款赠金金额")
    private BigDecimal bonusAmount = new BigDecimal(0);

    /** 退款后会员卡余额（本金） */
    @ApiModelProperty(value = "退款后会员卡余额（含赠送金额）")
    private BigDecimal currentReturnPrincipal = new BigDecimal(0);

    /** 剩余会员卡余额（含赠送金额） */
    @ApiModelProperty(value = "剩余会员卡余额（含赠送金额）")
    private BigDecimal currentAmount = new BigDecimal(0);

    /** 入账方式 */
    @ApiModelProperty(value = "入账方式")
    private Integer paymentId;

    /** 入账方式名称 */
    @ApiModelProperty(value = "入账方式名称")
    private String paymentManner;

    /** 退款人 */
    @ApiModelProperty(value = "退款人")
    private String operatorUserName;

    /** 退款原因*/
    @ApiModelProperty(value = "退款原因")
    private String remarks;
}