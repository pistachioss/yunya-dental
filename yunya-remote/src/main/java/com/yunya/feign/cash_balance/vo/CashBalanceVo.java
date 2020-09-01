package com.yunya.feign.cash_balance.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@ApiModel(value = "现金结存返回模型")
public class CashBalanceVo {

    @ApiModelProperty(value = "主键id",required = true)
    private Integer id;
    @ApiModelProperty(value = "结存日期",required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date crtTime;
    @ApiModelProperty(value = "期初现金结余",required = true)
    private BigDecimal cashFirst;
    @ApiModelProperty(value = "期末现金结余",required = true)
    private BigDecimal cashEnd;
    @ApiModelProperty(value = "今日存款",required = true)
    private BigDecimal amountDeposited;
    @ApiModelProperty(value = "差额调整",required = true)
    private BigDecimal balanceAdjustment;
    @ApiModelProperty(value = "结存人",required = true)
    private Integer crtId;
}

