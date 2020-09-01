package com.yunya.feign.cash_balance.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@ApiModel(value = "回显公司目标")
public class SpecialistTargetByIdVo {
    @ApiModelProperty(value = "主键id",required = true)
    private Integer id;
    @ApiModelProperty(value = "目标实收金额",required = true)
    private BigDecimal target;
    @ApiModelProperty(value = "实收金额",required = true)
    private BigDecimal complete;
    @ApiModelProperty(value = "专科项目id",required = true)
    private Integer ssgId;
    @ApiModelProperty(value = "日期",required = true)
    private Date date;
}
