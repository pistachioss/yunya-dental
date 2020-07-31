package com.yunya.feign.emr.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author xiangyang
 * @date 2020/7/29
 */
@ApiModel(value = "新增普通模板模型（词条，范句，要点，诊断）")
@Setter
@Getter
public class GeneralTemplateModel {

    @ApiModelProperty(value = "模板内容", required = true)
    @NotBlank
    @Size(max = 1000)
    private String content;
}
