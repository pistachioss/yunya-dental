package com.yunya.feign.report.domain.query.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author: chenlin
 * @date: 2023/10/16 17:54
 * @description: 员工+日期范围查询模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("员工+日期范围查询模型")
public class EmployeeDateRangeQueryForm extends DateRangeQueryForm {

    /** 员工id */
    @ApiModelProperty(value = "员工id", required = true)
    @NotNull(message = "员工id不能为空")
    private Integer employeeId;
}
