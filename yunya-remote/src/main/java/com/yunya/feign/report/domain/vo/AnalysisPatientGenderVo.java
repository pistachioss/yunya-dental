package com.yunya.feign.report.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介:欠费查询QueryForm
 *
 * @author: WY
 * @date: 2020/10/28 10:57
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class AnalysisPatientGenderVo implements Serializable {

    /** 性别 */
    private String gender;

    /** 男女数量 */
    private Integer countGender;

    /** 占比 */
    private String percentage;

}