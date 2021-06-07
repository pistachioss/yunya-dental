package com.yunya.feign.appointment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 线上预约消息通知
 * @author: LHB
 * @create: 2021-05-28 17:50
 **/
@Data
@ApiModel(value = "OnlineAppointNewMessageNoticeVo",description = "线上预约消息通知")
public class OnlineAppointNewMessageNoticeVo implements Serializable {
    @ApiModelProperty("上一次查询时间戳")
    private Long lastTimeStamp;

    @ApiModelProperty("消息数")
    private Integer count;
}
