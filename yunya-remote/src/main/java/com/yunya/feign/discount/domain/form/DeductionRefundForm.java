package com.yunya.feign.discount.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author xiangyang
 * @date 2023/8/29
 */
@Getter
@Setter
@ApiModel(value = "划扣卡券退费对象")
public class DeductionRefundForm {
    @ApiModelProperty(value = "卡券id", required = true)
    @NotNull
    private Integer cardId;
    @ApiModelProperty(value = "订单id", required = true)
    @NotNull
    private Integer orderId;
    @ApiModelProperty(value = "订单id", required = true)
    @NotNull
    private BigDecimal amount;
}
