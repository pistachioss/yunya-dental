package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 简介:
 *
 * @author: WY
 * @date: 2021/3/25 13:59
 * @description:
 * @since: 1.0.0
 */
@Data
@ApiModel("已收工作量明细返回模板")
public class ReceivedWorkloadDetailsVo {

    /**
     * 订单id
     */
    private Integer billId;

    /**
     * 患者id
     */
    private String patientId;

    /**
     * 关联时间
     */
    private String relatedTime;

    /**
     * 患者姓名
     */
    private String name;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     *  初诊日期
     */
    private String firstVisitDate;

    /**
     * 末诊日期
     */
    private String lastVisitDate;

    /**
     * 账单日期
     */
    private String orderDate;

    /**
     * 就诊门诊
     */
    private String abbreviation;

    /**
     * 挂号医生
     */
    private String employeeName;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 已收工作量
     */
    private BigDecimal workload;
}