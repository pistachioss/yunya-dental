package com.yunya.feign.report.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介:就诊患者分析 患者年龄Vo
 *
 * @author: WY
 * @date: 2020/10/28 10:57
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class AnalysisPatientAgeVo implements Serializable {

    /** 年龄段 */
    private String ageBracket;

    /** 占比 */
    private String percentage;

}