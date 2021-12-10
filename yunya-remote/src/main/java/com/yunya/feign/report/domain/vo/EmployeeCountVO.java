package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：员工数量VO
 *
 * @author: chenlin
 * @Description: 员工数量VO
 * @Date: 2021/12/8 13:46
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("员工数量VO")
public class EmployeeCountVO extends ClinicEmployeeReportVO implements Serializable {

    @ApiModelProperty("数量")
    private Integer count;
}
