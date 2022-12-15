package com.yunya.feign.appointment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(value = "预约流量管理返回")
@Data
public class ReservationLimitVO {

    @ApiModelProperty(value = "预约流量明细")
    private List<ReservationLimitDetailVO> details;

}
