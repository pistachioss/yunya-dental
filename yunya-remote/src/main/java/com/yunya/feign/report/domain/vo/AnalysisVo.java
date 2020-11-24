package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简介:就诊患者分析Vo
 *
 * @author: WY
 * @date: 2020/10/29 09:33
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("就诊患者分析Vo")
public class AnalysisVo implements Serializable {

    /** 患者来源分析 */
    @ApiModelProperty("患者来源分析")
    private List<AnalysisPatientOriginVo> analysisPatientOriginVoList;

    /** 患者性别 */
    @ApiModelProperty("患者性别")
    private List<AnalysisPatientGenderVo> analysisPatientGenderVoList;

    /** 患者年龄 */
    @ApiModelProperty("患者年龄")
    private List<AnalysisPatientAgeVo> analysisPatientAgeVoList;
}