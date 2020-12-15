package com.yunya.feign.appointment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: APP预约列表指定时间段内每天每个医生患者数量
 * @author: LHB
 * @create: 2020-11-02 19:46
 **/
@ApiModel(value = "AppointmentForMonthVo",description = "APP预约列表指定时间段内每天每个医生患者数量")
@Data
public class AppointmentForMonthVo implements Serializable {
    /** 当前时间 */
    @ApiModelProperty(value = "当前时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date currentDate;
    /** 当天患者数量 */
    @ApiModelProperty(value = "当天患者数量")
    private Integer count;
}
