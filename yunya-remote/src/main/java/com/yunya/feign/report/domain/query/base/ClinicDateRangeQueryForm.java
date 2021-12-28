package com.yunya.feign.report.domain.query.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 单门诊+日期范围查询参数模型
 *
 * @author: chenl
 * @date: 2021/12/27 13:48
 * @description: 单门诊+日期范围查询参数模型
 * @since: 1.0.0
 */

@Data
@ToString
@ApiModel("单门诊+日期范围查询参数模型")
public class ClinicDateRangeQueryForm extends DateRangeQueryForm implements Serializable {
    /** 门诊ID */
    @ApiModelProperty(value = "门诊ID", required = true)
    @NotNull(message = "门诊ID不能为空")
    private Integer orgId;

}