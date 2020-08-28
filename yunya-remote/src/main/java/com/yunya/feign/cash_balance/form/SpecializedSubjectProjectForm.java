package com.yunya.feign.cash_balance.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(value = "专科设置修改模型")
public class SpecializedSubjectProjectForm {


    @ApiModelProperty(value = "主键id",required = true)
    private int id;

    @ApiModelProperty(value = "专科项目名称",required = true)
    private int SpecializedSubjectProjectName;

    @ApiModelProperty(value = "子项目多个,拼接",required = true)
    private int subitems;
}

