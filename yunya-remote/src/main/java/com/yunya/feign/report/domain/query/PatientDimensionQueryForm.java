package com.yunya.feign.report.domain.query;

import com.yunya.feign.report.domain.query.base.DateRangeQueryForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简介：患者维度查询VO
 *
 * @author: chenlin
 * @Description: 患者维度查询VO
 * @Date: 2021/12/1 13:21
 * @since: 1.0.0
 */
@ApiModel("患者维度查询VO")
@ToString
@Data
public class PatientDimensionQueryForm extends DateRangeQueryForm implements Serializable {
    /** 患者姓名 */
    @ApiModelProperty(value = "患者姓名")
    private String patientName;

    /** 患者id列表*/
    @ApiModelProperty("患者id列表")
    private List<Integer> patientIds;
}
