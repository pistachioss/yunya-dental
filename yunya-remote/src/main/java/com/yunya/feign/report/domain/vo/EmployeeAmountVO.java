package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介：员工金额VO
 *
 * @author: chenlin
 * @Description: 员工金额VO
 * @Date: 2021/12/8 16:06
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("员工金额VO")
public class EmployeeAmountVO extends ClinicEmployeeReportVO implements Serializable {

    @ApiModelProperty("金额")
    private BigDecimal amount;
}
