package com.yunya.feign.appointment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel(value = "预约流量管理查询参数")
@Data
public class ReservationLimitQuery {

    @ApiModelProperty(value = "门诊ID", required = true)
    @NotNull(message = "门诊id不能为空")
    private Integer orgId;

    @ApiModelProperty(value = "配置日期(yyyy-dd)", required = true)
    @NotBlank(message = "配置日期不能为空")
    private String configDate;
}
