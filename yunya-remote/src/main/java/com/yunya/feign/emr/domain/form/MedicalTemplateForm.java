package com.yunya.feign.emr.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author xiangyang
 * @date 2020/7/29
 */
@Setter
@Getter
@ApiModel(value = "修改病例模板模型")
public class MedicalTemplateForm {

    @ApiModelProperty(value = "模板名称", required = true)
    @NotBlank
    @Size(max = 25)
    private String name;

    @ApiModelProperty(value = "模板类型", required = true)
    @NotNull
    private Integer type;

    @ApiModelProperty(value = "主诉")
    @NotBlank
    @Size(max = 1000)
    private String chiefComplaint;

    @ApiModelProperty(value = "现病史")
    @NotBlank
    @Size(max = 1000)
    private String presentIllness;

    @ApiModelProperty(value = "既往史")
    @NotBlank
    @Size(max = 1000)
    private String pastHistory;

    @ApiModelProperty(value = "检查")
    @NotBlank
    @Size(max = 1000)
    private String examination;

    @ApiModelProperty(value = "诊断")
    @NotBlank
    @Size(max = 1000)
    private String diagnosis;

    @ApiModelProperty(value = "计划")
    @NotBlank
    @Size(max = 1000)
    private String plan;

    @ApiModelProperty(value = "处理")
    @NotBlank
    @Size(max = 1000)
    private String treatment;

}
