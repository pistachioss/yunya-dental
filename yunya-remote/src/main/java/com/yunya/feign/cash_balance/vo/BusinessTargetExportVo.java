package com.yunya.feign.cash_balance.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@ApiModel(value = "导出业务目标")
public class BusinessTargetExportVo {
    @ApiModelProperty(value = "主键id",required = true)
    private Integer id;
    @ApiModelProperty(value = "目标实收金额",required = true)
    private BigDecimal targetCash;
    @ApiModelProperty(value = "完成实收金额",required = true)
    private BigDecimal completeCash;
    @ApiModelProperty(value = "目标工作量",required = true)
    private BigDecimal targetNum;
    @ApiModelProperty(value = "完成实收金额",required = true)
    private BigDecimal completeNum;
    @ApiModelProperty(value = "目标初诊人数",required = true)
    private BigDecimal targetFirstVisit;
    @ApiModelProperty(value = "完成实收金额",required = true)
    private BigDecimal completeFirstVisit;
    @ApiModelProperty(value = "目标就诊人次",required = true)
    private BigDecimal targetPatientNum;
    @ApiModelProperty(value = "完成实收金额",required = true)
    private BigDecimal completePatientNum;
    @ApiModelProperty(value = "日期",required = true)
    private Date date;
}
