package com.yunya.feign.clinic_base.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@ApiModel(value = "回显公司目标")
public class BusinessTargetByIdVo {
    @ApiModelProperty(value = "主键id",required = true)
    private Integer id;
    @ApiModelProperty(value = "目标实收金额",required = true)
    private BigDecimal targetCash;
    @ApiModelProperty(value = "目标工作量",required = true)
    private BigDecimal targetNum;
    @ApiModelProperty(value = "目标初诊人数",required = true)
    private Integer targetFirstVisit;
    @ApiModelProperty(value = "目标就诊人次",required = true)
    private Integer targetPatientNum;
    @ApiModelProperty(value = "日期",required = true)
    private Date date;
}
