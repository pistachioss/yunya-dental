package com.yunya.feign.appointment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel(value = "预约流量管理查询参数")
@Data
public class ReservationLimitAppQuery {
    @ApiModelProperty(value = "门诊名称", required = true)
    @NotBlank(message = "门诊不能为空")
    private String orgName;
}
