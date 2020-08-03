package com.yunya.feign.emr.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author xiangyang
 * @date 2020/7/30
 */
@Getter
@Setter
@ApiModel("病例模板详情模型")
public class MedicalDetailDetailVo {
    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "模板名称")
    private String name;

    @ApiModelProperty(value = "模板类型")
    private String type;

    @ApiModelProperty(value = "主诉")
    private String chiefComplaint;

    @ApiModelProperty(value = "现病史")
    private String presentIllness;

    @ApiModelProperty(value = "既往史")
    private String pastHistory;

    @ApiModelProperty(value = "检查")
    private String examination;

    @ApiModelProperty(value = "诊断")
    private String diagnosis;

    @ApiModelProperty(value = "计划")
    private String plan;

    @ApiModelProperty(value = "处理")
    private String treatment;
}
