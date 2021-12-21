package com.yunya.feign.report.domain.query.base;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介：两个日期范围查询参数模型
 *
 * @author: chenlin
 * @Description: 两个日期范围查询参数模型
 * @Date: 2021/12/10 10:12
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("两个日期范围查询参数模型")
public class DoubleDateRangeQueryForm extends PageQuery implements Serializable {
    /** 第一套时间类型 */
    @ApiModelProperty(value = "第一套时间类型:0-日；1-月；2-年", required = true)
    @NotNull(message = "第一个时间类型不能为空！")
    private Byte dateType;
    /** 第一套查询时间 */
    @ApiModelProperty(value = "第一套查询开始时间", required = true)
    @NotBlank(message = "第一个查询开始时间不能为空！")
    private String startDate1;
    /** 第一套查询结束时间 */
    @ApiModelProperty(value = "第一套查询结束时间", required = true)
    @NotBlank(message = "第一个查询结束时间不能为空！")
    private String endDate1;
    /** 第一套查询时间 */
    @ApiModelProperty(value = "第一套查询开始时间数字形式：yyyyMMdd")
    private Integer sDateInt1;
    /** 第一套查询结束时间 */
    @ApiModelProperty(value = "第一套查询结束时间数字形式：yyyyMMdd")
    private Integer eDateInt1;

    /** 第二套查询时间 */
    @ApiModelProperty(value = "第二套查询开始时间", required = true)
    @NotBlank(message = "第二个查询开始时间不能为空！")
    private String startDate2;
    /** 第二套查询结束时间 */
    @ApiModelProperty(value = "第二套查询结束时间", required = true)
    @NotBlank(message = "第二个查询结束时间不能为空！")
    private String endDate2;
    /** 第二套查询时间 */
    @ApiModelProperty(value = "第二套查询开始时间数字形式：yyyyMMdd")
    private Integer sDateInt2;
    /** 第二套查询结束时间 */
    @ApiModelProperty(value = "第二套查询结束时间数字形式：yyyyMMdd")
    private Integer eDateInt2;
}
