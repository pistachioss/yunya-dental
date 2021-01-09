package com.yunya.feign.report.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 预约统计查询参数
 * @author: LHB
 * @create: 2021-01-08 15:24
 **/
@ApiModel(value = "AppointmentCountQuery",description = "预约统计查询参数")
@Data
public class AppointmentCountQuery implements Serializable {
    @ApiModelProperty("开始时间")
    private String startDate;
    @ApiModelProperty("结束时间")
    private String endDate;
    @ApiModelProperty("门诊ID")
    private Integer orgId;
    @ApiModelProperty("医生ID")
    private Integer dentistId;
}
