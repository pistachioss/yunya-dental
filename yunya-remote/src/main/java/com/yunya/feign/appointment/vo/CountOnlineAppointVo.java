package com.yunya.feign.appointment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description:
 * @author: LHB
 * @create: 2021-06-01 17:15
 **/
@ApiModel("")
@Data
public class CountOnlineAppointVo implements Serializable {
    @ApiModelProperty("时间")
    private String time;
    @ApiModelProperty("预约人数")
    private Integer count;
}
