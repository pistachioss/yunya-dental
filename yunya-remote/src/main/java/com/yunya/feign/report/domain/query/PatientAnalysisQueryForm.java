package com.yunya.feign.report.domain.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 简介: 患者报表查询-就诊患者分析Form
 *
 * @author: WY
 * @date: 2020/10/27 13:48
 * @description:
 * @since: 1.0.0
 */

@Data
@ToString
public class PatientAnalysisQueryForm implements Serializable {

    @ApiModelProperty(value = "门诊id", required = false)
    /** 门诊id */
    private Integer orgId;

    @ApiModelProperty(value = "(年-月-日)开始日期yyyy-MM-dd", required = false)
    /** 开始（年-月-日） */
    private String startDate;

    @ApiModelProperty(value = "(年-月-日)结束日期yyyy-MM-dd", required = false)
    /** 结束（年-月-日） */
    private String endDate;

    @ApiModelProperty(value = "(年-月):yyyy-MM", required = false)
    /** 年月 */
    private String monthDay;

    @ApiModelProperty(value = "(年):yyyy", required = false)
    /** 年 */
    private String year;

}