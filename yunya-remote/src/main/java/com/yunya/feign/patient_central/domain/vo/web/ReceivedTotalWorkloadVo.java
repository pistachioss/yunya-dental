package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 简介: 患者消费项目工作量
 *
 * @author: WY
 * @date: 2021/3/23 10:28
 * @description: 患者消费项目工作量模型
 * @since: 1.0.0
 */
@ApiModel(value = "PrintInfoVo",description = "患者消费项目工作量模型")
@Data
public class ReceivedTotalWorkloadVo {

    /**
     * 订单id
     */
    private Integer billId;

    /**
     * 患者id
     */
    private Integer patientId;

    /**
     * 订单总实收
     */
    private BigDecimal actualAmount;

    /**
     *  （sum（（单价 * 数量） / 订单总实收））订单总实收（多个项目）
     */
    private BigDecimal orderWorkload;
}