package com.yunya.feign.ivy_mini.domain.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/16
 * @description:
 */
@Data
public class DistributionForm {
    /**
     * 起送价格
     */
    @ApiModelProperty(value = "起送价格")
    private BigDecimal startSendingPrice;

    /**
     * 配送费用
     */
    @ApiModelProperty(value = "配送费用")
    private BigDecimal sendingPrice;
}
