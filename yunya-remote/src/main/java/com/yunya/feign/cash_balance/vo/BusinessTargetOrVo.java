package com.yunya.feign.cash_balance.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(value = "返回模型")
public class BusinessTargetOrVo {

    @ApiModelProperty(value = "主键id",required = true)
    private Integer id;

}
