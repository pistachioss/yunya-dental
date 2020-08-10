package com.yunya.feign.emr.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

/**
 * @author xiangyang
 * @date 2020/7/29
 */
@Setter
@Getter
@ApiModel(value = "新增病例模板模型")
public class MedicalTemplateModel {

    @ApiModelProperty(value = "模板名称", required = true)
    @NotBlank
    @Size(max = 25)
    private String name;

    @ApiModelProperty(value = "模板类型（0：初诊  1：复诊）", required = true, example = "0：初诊  1：复诊")
    @NotNull
    private Integer type;

    @ApiModelProperty(value = "复诊")
    @Size(max = 1000)
    private String reExamination;

    @ApiModelProperty(value = "主诉")
    @Size(max = 1000)
    private String chiefComplaint;

    @ApiModelProperty(value = "现病史")
    @Size(max = 1000)
    private String presentIllness;

    @ApiModelProperty(value = "既往史")
    @Size(max = 1000)
    private String pastHistory;

    @ApiModelProperty(value = "检查")
    @Size(max = 1000)
    private String examination;

    @ApiModelProperty(value = "诊断")
    @Size(max = 1000)
    private String diagnosis;

    @ApiModelProperty(value = "计划")
    @Size(max = 1000)
    private String plan;

    @ApiModelProperty(value = "处理")
    @Size(max = 1000)
    private String treatment;

    @ApiModelProperty(value = "处方")
    @Size(max = 1000)
    private String prescription;

}
