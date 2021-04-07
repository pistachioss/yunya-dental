package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author YK
 */
@ApiModel(value = "支付方式/免单支付方式model")
@Data
public class BaseBillPayVo {
    /**
     * 订单收费记录ID
     */
    private Integer billPayId;

    /**
     * 订单ID
     */
    private Integer billId;

    /**
     * 本次收费总额
     */
    private BigDecimal receivedAmount;

}