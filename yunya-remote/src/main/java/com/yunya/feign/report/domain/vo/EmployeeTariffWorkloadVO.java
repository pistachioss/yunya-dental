package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介：员工项目的工作量统计VO
 *
 * @author: chenlin
 * @Description: 员工项目的工作量统计VO
 * @Date: 2021/10/26 12:13
 * @since: 1.0.0
 */
@Data
@ApiModel("员工项目的工作量统计VO")
@ToString
@EqualsAndHashCode(callSuper = true)
public class EmployeeTariffWorkloadVO extends ClinicEmployeTariffInfoVO implements Serializable {
    @ApiModelProperty("数量")
    private Integer quantity;

    @ApiModelProperty("工作量")
    private BigDecimal workload;
}