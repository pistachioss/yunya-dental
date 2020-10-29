package com.yunya.feign.report.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介:欠费查询QueryForm
 *
 * @author: WY
 * @date: 2020/10/28 10:57
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class ArrearsVo implements Serializable {

    /** 账单id */
    private Integer billId;

    /** 挂号医生 */
    private String employeeName;

    /** 开单日期 */
    private String orderDate;

    /** 账单编号 */
    private String orderNum;

    /** 患者姓名 */
    private String name;

    /** 手机号 */
    private String mobile;

    /** 原价合计 */
    private BigDecimal originalPrice;

    /** 优惠金额 */
    private BigDecimal privilegeAmount;

    /** 实收金额 */
    private BigDecimal actualAmount;

    /** 已收金额 */
    private BigDecimal receivedAmount;

    /** 剩余欠费金额 */
    private BigDecimal debtAmount;

}