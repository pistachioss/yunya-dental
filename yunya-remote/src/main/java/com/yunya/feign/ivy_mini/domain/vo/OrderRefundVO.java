package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description:
 * @author: xy
 * @date 2022/7/1 10:04
 **/
@Data
@ApiModel(description = "订单退款返回")
public class OrderRefundVO {
    @ApiModelProperty(value = "订单")
    private PayOrderVO orderVO;
}
