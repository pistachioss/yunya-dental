package com.yunya.feign.cash_balance.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(value = "现金结存修改模型")
public class CashBalanceForm {


    @ApiModelProperty(value = "主键id",required = true)
    private int id;
    @ApiModelProperty(value = "当前期初金额",required = true)
    private int cashFirst;
    @ApiModelProperty(value = "本日现金存款",required = true)
    private int amountDeposited;
    @ApiModelProperty(value = "差额调整",required = true)
    private int balanceAdjustment;
    @ApiModelProperty(value = "差额调整备注",required = true)
    private String balanceAdjustmentRemark;
    @ApiModelProperty(value = "图片uri",required = true)
    private String uri;
}

