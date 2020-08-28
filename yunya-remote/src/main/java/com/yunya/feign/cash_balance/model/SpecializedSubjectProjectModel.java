package com.yunya.feign.cash_balance.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(value = "专科设置添加模型")
public class SpecializedSubjectProjectModel {


    @ApiModelProperty(value = "专科项目名称",required = true)
    private String specializedSubjectProjectName;
    @ApiModelProperty(value = "子项目多个,拼接",required = true)
    private String subitems;
}

