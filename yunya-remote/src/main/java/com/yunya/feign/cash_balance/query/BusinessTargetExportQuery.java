package com.yunya.feign.cash_balance.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@ApiModel(value = "业务目标导出模型")
public class BusinessTargetExportQuery {

    @ApiModelProperty(value = "日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM", timezone = "GMT+8")
    private Date date;
}
