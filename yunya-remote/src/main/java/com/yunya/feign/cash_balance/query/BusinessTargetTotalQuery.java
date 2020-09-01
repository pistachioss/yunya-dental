package com.yunya.feign.cash_balance.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@ApiModel(value = "根据门诊id获取列表")
public class BusinessTargetTotalQuery {
    @ApiModelProperty(value = "门诊id",required = true)
    private List<Integer> ids;

}
