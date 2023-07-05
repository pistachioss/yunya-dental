package com.yunya.feign.discount.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author: xy
 * @date 20232/6/28
 **/
@Data
@ApiModel(description = "创建划扣订单明细")
public class CouponOrderDetailModel {
    @ApiModelProperty(value = "礼包id", required = true)
    @NotNull
    private Integer couponId;
    @ApiModelProperty(value = "礼包名称", required = true)
    @NotBlank
    private String couponName;
    @ApiModelProperty(value = "数量", required = true)
    @NotNull
    private Integer quantity;
    @ApiModelProperty(value = "执行人ID", required = true)
    @NotNull
    private Integer executorId;
    @ApiModelProperty(value = "咨询师ID")
    private Integer consulterId;
    @ApiModelProperty("备注")
    @Size(max = 50)
    private String remark;

}
