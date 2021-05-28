package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介:
 *
 * @author: WY
 * @date: 2021/5/28 10:16
 * @description:
 * @since: 1.0.0
 */
@ApiModel(value = "MarketRecommendationDetailedVo",description = "市场推荐列表返回模板")
@Data
public class MarketRecommendationDetailedVo implements Serializable {

    /** 患者姓名 */
    private String patientName;

    /** 病历号 */
    private String medicalRecordNumber;

    /** 收费日期 */
    private String payDate;

    /** 账单日期 */
    private String orderDate;

    /** 实收金额 */
    private BigDecimal actualAmount;

    /** 其中免单支付 */
    private BigDecimal freePayment;

    /** 退费金额 */
    private BigDecimal refundAmount;
}