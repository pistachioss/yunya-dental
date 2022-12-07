package com.yunya.feign.appointment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@ApiModel(value = "预约登记流量管理参数")
@Data
public class ReservationLimitModel {

    @ApiModelProperty(value = "门诊名称", required = true)
    @NotBlank(message = "门诊不能为空")
    private String orgName;

    @ApiModelProperty(value = "预约流量明细", required = true)
    @NotEmpty(message = "预约流量明细不能为空")
    private List<ReservationLimitDetailModel> details;
}
