package com.yunya.feign.clinic_base.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@ApiModel(value = "添加业务目标")
public class BusinessTargetModel {

    @ApiModelProperty(value = "日期类型 0月 1年",required = true)
    private Integer dateType;
    @ApiModelProperty(value = "日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date date;
    @ApiModelProperty(value = "目标实收金额",required = true)
    private BigDecimal targetCash;
    @ApiModelProperty(value = "目标工作量",required = true)
    private BigDecimal targetNum;
    @ApiModelProperty(value = "目标初诊人数",required = true)
    private BigDecimal targetFirstVisit;
    @ApiModelProperty(value = "目标就诊人次",required = true)
    private BigDecimal targetPatientNum;
    @ApiModelProperty(value = "团队类型 0个人 1门诊",required = true)
    private String teamType;
    @ApiModelProperty(value = "团队类型id",required = true)
    private Integer numId;

}
