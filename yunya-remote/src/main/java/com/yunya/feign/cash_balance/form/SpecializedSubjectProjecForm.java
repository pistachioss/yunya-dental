package com.yunya.feign.cash_balance.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(value = "专科设置修改模型")
public class SpecializedSubjectProjecForm {


    @ApiModelProperty(value = "主键id",required = true)
    private int id;

    @ApiModelProperty(value = "专科项目名称",required = true)
    private int SpecializedSubjectProjecName;

    @ApiModelProperty(value = "子项目",required = true)
    private int subitems;
}

