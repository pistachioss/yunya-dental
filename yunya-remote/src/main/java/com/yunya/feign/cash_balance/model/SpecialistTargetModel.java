package com.yunya.feign.cash_balance.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

@Setter
@Getter
@ApiModel(value = "添加专科数量目标")
public class SpecialistTargetModel {
    @ApiModelProperty(value = "日期类型 0月 1年",required = true)
    private Integer dateType;
    @ApiModelProperty(value = "日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM", timezone = "GMT+8")
    private Date date;
    @ApiModelProperty(value = "专科项目id",required = true)
    private Integer sspId;
    @ApiModelProperty(value = "目标",required = true)
    private BigDecimal target;
    @ApiModelProperty(value = "团队类型 0个人 1门诊",required = true)
    private String teamType;
    @ApiModelProperty(value = "团队类型id",required = true)
    private Integer numId;
}
