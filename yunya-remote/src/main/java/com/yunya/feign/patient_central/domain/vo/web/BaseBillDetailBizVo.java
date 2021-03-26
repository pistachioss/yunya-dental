package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 简介:
 *
 * @author: WY
 * @date: 2021/3/25 15:58
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("返回订单项目信息参数模型")
public class BaseBillDetailBizVo {

    /**
     * 订单id
     */
    private Integer billId;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 单个项目实收金额
     */
    private BigDecimal actualAmount;
}