package com.yunya.feign.appointment.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@ApiModel(value = "预约登记流量当日详情参数")
@Data
public class ReservationLimitDetailModel {

    @ApiModelProperty("预约流量管理ID")
    private Integer id;

    @ApiModelProperty(value = "配置日期", required = true)
    @NotNull(message = "配置日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate configDate;

    @ApiModelProperty(value = "配置数量限制", required = true)
    @Min(value = 0, message = "配置数量不能低于0")
    private Integer configLimit;

}
