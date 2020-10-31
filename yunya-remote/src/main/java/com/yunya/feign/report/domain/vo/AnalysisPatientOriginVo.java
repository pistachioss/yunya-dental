package com.yunya.feign.report.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 患者报表-就诊患者分析-患者类型Vo
 *
 * @author: WY
 * @date: 2020/10/29 11:11
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class AnalysisPatientOriginVo implements Serializable {

    /** 患者来源类别 */
    private Integer originType;

    /** 患者来源名称 */
    private String originTypeName;

    /** 患者来源类别数量 */
    private Integer countType;

    /** 占比 */
    private String percentage;

}