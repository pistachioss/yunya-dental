package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

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
public class PatientDimensionQueryForm extends PageQuery implements Serializable {
    /** 时间类型 */
    @ApiModelProperty(value = "时间类型:0-日；1-月；2-年", required = true)
    @NotNull(message = "时间类型不能为空！")
    private Byte dateType;
    /** 查询时间 */
    @ApiModelProperty(value = "查询开始时间", required = true)
    @NotBlank(message = "查询开始时间不能为空！")
    private String startDate;
    /** 查询结束时间 */
    @ApiModelProperty(value = "查询结束时间", required = true)
    @NotBlank(message = "查询结束时间不能为空！")
    private String endDate;
    /** 患者姓名 */
    @ApiModelProperty(value = "患者姓名")
    private String patientName;
}
