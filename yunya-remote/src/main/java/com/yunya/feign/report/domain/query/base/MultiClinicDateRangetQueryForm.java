package com.yunya.feign.report.domain.query.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 简介: 多门诊+日期范围查询参数模型
 *
 * @author: chenl
 * @date: 2021/12/07 13:48
 * @description:
 * @since: 1.0.0
 */

@Data
@ToString
@ApiModel("多门诊+日期范围查询参数模型")
public class MultiClinicDateRangetQueryForm extends DateRangeQueryForm implements Serializable {
    /** 门诊ID列表 */
    @ApiModelProperty(value = "门诊ID列表", required = true)
    @NotNull(message = "门诊ID不能为空")
    private List<Integer> orgIds;

}