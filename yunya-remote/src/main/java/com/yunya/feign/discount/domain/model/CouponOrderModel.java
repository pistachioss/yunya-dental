package com.yunya.feign.discount.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author: xy
 * @date 20232/6/28
 **/
@Data
@ApiModel(description = "创建划扣订单")
public class CouponOrderModel {
    @ApiModelProperty(value = "患者id", required = true)
    @NotNull
    private Integer patientId;
    @ApiModelProperty(value = "礼包明细")
    @NotEmpty
    private List<CouponOrderDetailModel> detail;

}
