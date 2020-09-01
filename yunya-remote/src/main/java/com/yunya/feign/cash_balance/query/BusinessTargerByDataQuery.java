package com.yunya.feign.cash_balance.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@ApiModel(value = "回显公司目标")
public class BusinessTargerByDataQuery {

    @ApiModelProperty(value = "日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date date;
    @ApiModelProperty(value = "团队类型 0个人 1门诊",required = true)
    private Integer teamType;
    @ApiModelProperty(value = "团队类型id",required = true)
    private Integer numId;

}
