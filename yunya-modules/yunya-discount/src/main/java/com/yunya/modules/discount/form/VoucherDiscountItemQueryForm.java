package com.yunya.modules.discount.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


/**
 * @author 杨柳絮
 * @className VoucherDiscountItemQueryForm
 * @description
 * @date 2020/8/26 16:42
 */
@Data
public class VoucherDiscountItemQueryForm {
    /**
     * 优惠券id
     */
    @NotNull(message = "优惠券id不能为空")
    @ApiModelProperty(value = "优惠券id", required = true)
    private Integer couponId;
}
