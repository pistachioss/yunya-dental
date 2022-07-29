package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description:
 * @author: xy
 * @date 2022/6/15 17:27
 **/
@Data
public class CalcAmountVO {
    @ApiModelProperty("运费")
    private BigDecimal freightAmount;
    @ApiModelProperty("应付金额")
    private BigDecimal payAmount;
    @ApiModelProperty("订单商品总金额")
    private BigDecimal totalAmount;
    @ApiModelProperty("起送费")
    private BigDecimal startSendingPrice;
}
