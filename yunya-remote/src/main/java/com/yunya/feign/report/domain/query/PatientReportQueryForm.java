package com.yunya.feign.report.domain.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.List;

/**
 * 简介: 患者报表查询-未复诊预约且未提醒Form
 *
 * @author: WY
 * @date: 2020/10/27 13:48
 * @description:
 * @since: 1.0.0
 */

@Data
@ToString
public class PatientReportQueryForm implements Serializable {

    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    @ApiModelProperty(value = "门诊id", required = false)
    /** 门诊id */
    private Integer orgId;

    @ApiModelProperty(value = "开始末次就诊日期", required = false)
    /** 充值开始日期 */
    private String startDate;

    @ApiModelProperty(value = "结束末次就诊日期", required = false)
    /** 充值结束日期 */
    private String endDate;

    @ApiModelProperty(value = "末次接诊医生ID列表", required = false)
    /** 接诊医生 */
    private List<Integer> attendingDoctors;

    @ApiModelProperty(value = "患者条件", required = false)
    /** 患者条件 */
    private String combination;

    @ApiModelProperty(value = "初复诊（0：初,1：复）", required = false)
    /** 出复诊类型 */
    private List<Integer> treatTypes;

}