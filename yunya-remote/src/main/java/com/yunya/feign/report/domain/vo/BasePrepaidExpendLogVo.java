package com.yunya.feign.report.domain.vo;

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
public class BasePrepaidExpendLogVo {

    /** 操作id **/
    private Integer occurLogId;

    /** 消费日期 */
    private String occurDate;

    /** 账单日期 */
    private String orderDate;

    /** 订单号 */
    private String orderNum;

    /** 患者姓名 */
    private String name;

    /** 手机号 */
    private String mobile;

    /** 主卡人姓名 */
    private String masterCardName;

    /** 预付款号 */
    private String cardNumber;

    /** 消费本金金额 */
    private BigDecimal principalAmount;

    /** 消费赠金金额 */
    private BigDecimal bonusAmount;

    /** 消费后会员卡余额（本金） */
    private BigDecimal currentRechargePrincipal;

    /** 消费后会员卡赠金（赠金） */
    private BigDecimal currentRechargeBonus;

    /** 收费人 */
    private String operatorUserName;

}