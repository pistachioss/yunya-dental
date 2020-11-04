package com.yunya.feign.discount.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author xiangyang
 * @date 2020/10/9
 */
@Getter
@Setter
@ApiModel(value = "授权项目明细")
public class AuthItemBenefitModel {
    @ApiModelProperty(value = "订单明细id", required = true)
    @NotNull
    private Integer orderDetailId;
    @ApiModelProperty(value = "项目id", required = true)
    @NotNull
    private Integer itemId;
    @ApiModelProperty(value = "项目类型", required = true)
    @NotNull
    private Integer type;
    @ApiModelProperty(value = "优惠金额", required = true)
    @NotNull
    @Min(value = 0)
    private BigDecimal benefitAmount;
}
