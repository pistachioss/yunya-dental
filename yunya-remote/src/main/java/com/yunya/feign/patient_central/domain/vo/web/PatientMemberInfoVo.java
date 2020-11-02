package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简单介绍:</br> 返回会员信息模型
 *
 * @author: WY
 * @date 2020/8/28 20:48
 * @description:
 * @since: 1.0.0
 */

@Data
@ToString
@ApiModel("返回会员信息模型")
public class PatientMemberInfoVo implements Serializable {
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 诊所id
     */
    private Integer orgId;

    /**
     * 患者id
     */
    private Integer patientId;

    /**
     * 患者id
     */
    private String name;

    /**
     * 会员账号
     */
    private String cardNumber;

    /**
     * 本金+赠金
     */
    private BigDecimal memberMoneySum;

    /**
     * 赠金
     */
    private BigDecimal principalAmount;

    /**
     * 本金
     */
    private BigDecimal bonusAmount;
}
