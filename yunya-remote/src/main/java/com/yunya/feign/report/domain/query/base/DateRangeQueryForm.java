package com.yunya.feign.report.domain.query.base;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介：日期范围查询参数模型
 *
 * @author: chenlin
 * @Description: 日期范围查询参数模型
 * @Date: 2021/12/10 10:12
 * @since: 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
@ApiModel("日期范围查询参数模型")
public class DateRangeQueryForm extends PageQuery implements Serializable {
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
    /** 查询时间 */
    @ApiModelProperty(value = "查询开始时间数字形式：yyyyMMdd")
    private Integer sDateInt;
    /** 查询结束时间 */
    @ApiModelProperty(value = "查询结束时间数字形式：yyyyMMdd")
    private Integer eDateInt;
}
