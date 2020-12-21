package com.yunya.feign.clinic_base.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@ApiModel(value = "专科设置修改模型")
public class SpecializedSubjectProjectForm {


    @ApiModelProperty(value = "主键id",required = true)
    @NotNull
    private int id;

    @ApiModelProperty(value = "专科项目名称",required = true)
    @NotNull
    private String SpecializedSubjectProjectName;

    @ApiModelProperty(value = "子项目多个,拼接",required = true)
    @NotNull
    private String subitems;
}

