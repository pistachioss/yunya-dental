package com.yunya.feign.appointment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel(value = "预约登记流量当日详情")
@Data
public class ReservationLimitDetailVO {

    @ApiModelProperty("预约流量管理ID")
    private Integer id;

    @ApiModelProperty(value = "配置日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date configDate;

    @ApiModelProperty(value = "配置数量限制")
    private Integer configLimit;

    @ApiModelProperty(value = "已提交预约意向登记数")
    private Long submitLimit;
}
