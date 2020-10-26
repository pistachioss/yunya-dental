package com.yunya.feign.report.domain.vo;

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
public class MemberExpendLogBizVo {

    /** 操作id **/
    private Integer occurLogId;

    /** 消费日期 */
    private String occurDate;

    /** 患者姓名 */
    private String name;

    /** 手机号 */
    private String mobile;

    /** 会员卡号 */
    private String cardNumber;

    /** 会员级别id */
    private Integer memberLevelId;

    /** 会员卡级别名称 */
    private String memberLevelName;

    /** 本金金额 */
    private BigDecimal principalAmount;

    /** 赠金金额 */
    private BigDecimal bonusAmount;

    /** 消费后会员卡余额（本金） */
    private BigDecimal currentRechargePrincipal;

    /** 消费后会员卡赠金（赠金） */
    private BigDecimal currentRechargeBonus;

    /** 入账方式 */
    private Integer paymentId;

    /** 入账方式名称 */
    private String paymentManner;

    /** 充值人 */
    private String operatorUserName;
}