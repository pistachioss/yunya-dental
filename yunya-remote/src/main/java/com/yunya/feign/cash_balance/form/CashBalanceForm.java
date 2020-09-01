package com.yunya.feign.cash_balance.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Setter
@Getter
@ApiModel(value = "现金结存修改模型")
public class CashBalanceForm {


    @ApiModelProperty(value = "主键id",required = true)
    @NotNull
    private int id;
    @ApiModelProperty(value = "当前期初金额",required = true)
    private BigDecimal cashFirst;
    @ApiModelProperty(value = "本日现金存款",required = true)
    private BigDecimal amountDeposited;
    @ApiModelProperty(value = "差额调整",required = true)
    private BigDecimal balanceAdjustment;
    @ApiModelProperty(value = "差额调整备注",required = true)
    private String balanceAdjustmentRemark;
    @ApiModelProperty(value = "图片uri",required = true)
    private String uri;
}

