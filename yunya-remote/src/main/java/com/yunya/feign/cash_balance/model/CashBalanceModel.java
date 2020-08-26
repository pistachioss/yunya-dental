package com.yunya.feign.cash_balance.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@ApiModel(value = "现金结存添加模型")
public class CashBalanceModel {


    @ApiModelProperty(value = "本日现金存款",required = true)
    private int amountDeposited;
    @ApiModelProperty(value = "差额调整",required = true)
    private int balanceAdjustment;
    @ApiModelProperty(value = "差额调整备注",required = true)
    private String balanceAdjustmentRemark;
    @ApiModelProperty(value = "图片uri",required = true)
    private String uri;
    @ApiModelProperty(value = "门诊id",required = true)
    private Integer orgId;
}

