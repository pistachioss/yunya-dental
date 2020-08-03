package com.yunya.feign.patient_central.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 简单介绍:</br>
 *
 * @author: WY
 * @date 2020/7/31 9:46
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class PatientPrepaymentsInfoVo implements Serializable {
    /**
     * 主键id
     */
    private Integer Id;

    /**
     * 诊所id
     */
    private Integer orgId;

    /**
     * 患者id
     */
    private Integer patientId;

    /**
     * 预付款账号
     */
    private String prepaymentNumber;

    /**
     * 预付款本金+预付款赠金
     */
    private BigDecimal PrepaymentMoneySum;

    /**
     * 开户日期
     */
    private Date crtTime;
}
