package com.yunya.feign.cash_balance.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@ApiModel(value = "现金结存返回模型")
public class SpecializedSubjectProjectVo {

    @ApiModelProperty(value = "主键id",required = true)
    private Integer id;
    @ApiModelProperty(value = "专科项目名称",required = true)
    private String SpecializedSubjectProjectName;
    @ApiModelProperty(value = "子项目多个,拼接",required = true)
    private String subitems;
}

