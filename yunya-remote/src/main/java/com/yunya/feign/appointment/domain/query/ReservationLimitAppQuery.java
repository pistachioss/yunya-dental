package com.yunya.feign.appointment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel(value = "预约流量管理查询参数")
@Data
public class ReservationLimitAppQuery {
    @ApiModelProperty(value = "门诊ID", required = true)
    @NotNull(message = "门诊id不能为空")
    private Integer orgId;
}
