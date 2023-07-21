package com.yunya.feign.discount.domain.form;

import com.yunya.feign.discount.domain.model.CouponOrderDetailModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "划扣订单修改")
public class CouponOrderForm {
    @ApiModelProperty("订单id")
    private Integer orderId;
    @ApiModelProperty("订单明细")
    private List<CouponOrderDetailModel> detail;
}
