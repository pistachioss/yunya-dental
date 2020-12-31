package com.yunya.feign.clinic_base.domain.model;

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
public class SpecialistProjectReportModel implements Serializable {

    /** 门诊orgIds */
    private List<Integer> orgIds;

    /** 条件类型：0.年月日 1.年月 2.年 */
    private Integer dateType;

    /** 开始（年-月-日） */
    private String startDate;

    /** 结束（年-月-日） */
    private String endDate;

    /** 项目ids */
    private String[] billingItemIds;
}