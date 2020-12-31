package com.yunya.feign.clinic_base.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 简介: 就诊患者分析-专科项目参数模板
 *
 * @author: WY
 * @date: 2020/12/30 16:14
 * @description:
 * @since: 1.0.0
 */
@ApiModel("就诊患者分析-专科项目参数模板")
@Data
public class SpecialistProjectReportForm implements Serializable {

    @ApiModelProperty(value = "门诊orgIds",required = true)
    /** 门诊orgIds */
    private List<Integer> orgIds;

    @ApiModelProperty(value = "条件类型：0.年月日 1.年月 2.年",required = true)
    /** 条件类型：0.年月日 1.年月 2.年 */
    private Integer dateType;

    @ApiModelProperty(value = "(年-月-日)开始日期yyyy-MM-dd", required = false)
    /** 开始（年-月-日） */
    private String startDate;

    @ApiModelProperty(value = "(年-月-日)结束日期yyyy-MM-dd", required = false)
    /** 结束（年-月-日） */
    private String endDate;
}