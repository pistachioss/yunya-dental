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
@ApiModel("预付款消费列表Vo")
public class BasePrepaidExpendLogVo {

    /** 操作id **/
    @ApiModelProperty("操作id")
    private Integer occurLogId;

    /** 消费日期 */
    @ApiModelProperty("消费日期")
    private String occurDate;

    /** 账单日期 */
    @ApiModelProperty("账单日期")
    private String orderDate;

    /** 订单号 */
    @ApiModelProperty("订单号")
    private String orderNum;

    /** 患者姓名 */
    @ApiModelProperty("患者姓名")
    private String name;

    /** 手机号 */
    @ApiModelProperty("手机号")
    private String mobile;

    /** 主卡人姓名 */
    @ApiModelProperty("主卡人姓名")
    private String masterCardName;

    /** 预付款号 */
    @ApiModelProperty("预付款号")
    private String cardNumber;

    /** 消费本金金额 */
    @ApiModelProperty("消费本金金额")
    private BigDecimal principalAmount = new BigDecimal(0);

    /** 消费赠金金额 */
    @ApiModelProperty("消费赠金金额")
    private BigDecimal bonusAmount = new BigDecimal(0);

    /** 消费后预付款余额（含赠送金额） */
    @ApiModelProperty("消费后预付款余额（含赠送金额）")
    private BigDecimal currentExpendPrincipal = new BigDecimal(0);

    /** 剩余预付款余额（含赠送金额） */
    @ApiModelProperty("剩余会员卡余额（含赠送金额）")
    private BigDecimal currentAmount = new BigDecimal(0);

    /** 收费人 */
    @ApiModelProperty("收费人")
    private String operatorUserName;

}