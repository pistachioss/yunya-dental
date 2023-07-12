package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "划扣订单详情")
public class CouponPayDetailVO {
    @ApiModelProperty("门诊id")
    private Integer orgId;
    @ApiModelProperty("门诊名称")
    private String orgName;
    @ApiModelProperty("实收金额")
    private BigDecimal receivedAmount;
    @ApiModelProperty("收费日期")
    private String payDate;

}
