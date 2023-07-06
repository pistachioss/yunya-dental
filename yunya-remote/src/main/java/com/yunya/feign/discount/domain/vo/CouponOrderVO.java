package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "划扣订单详情")
public class CouponOrderVO {
    @ApiModelProperty("订单id")
    private Integer orderId;
    @ApiModelProperty("应收总金额")
    private BigDecimal receivableAmount;
    @ApiModelProperty("订单明细")
    private List<CouponOrderDetailVO> detail;
}
