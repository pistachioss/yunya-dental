package com.yunya.feign.appointment.domain.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@ApiModel(value = "预约流量管理查询参数")
@Data
public class ReservationLimitQuery {

    @ApiModelProperty(value = "门诊ID", required = true)
    @NotNull(message = "门诊id不能为空")
    private Integer orgId;

    @ApiModelProperty(value = "配置日期(yyyy-dd)", required = true)
    @NotNull(message = "配置日期不能为空")
    @JsonFormat(pattern = "yyyy-MM",timezone = "GMT+8")
    private Date configDate;
}
