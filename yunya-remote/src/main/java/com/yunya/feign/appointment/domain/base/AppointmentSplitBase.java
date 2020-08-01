package com.yunya.feign.appointment.domain.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * 预约分解参数分装
 *
 * @author yunya-lihuibin
 * @create 2020-07-30 14:19
 * @update yunya-lihuibin    2020-07-30    新建
 */
@ApiModel("预约分解参数封装")
@Data
@ToString
public class AppointmentSplitBase {

    /** 组织id */
    @ApiModelProperty(value = "组织id", required = true)
    private Integer orgId;

    /** 预约id */
    @ApiModelProperty(value = "预约id", required = true)
    private Integer appointmentId;

    /** 拆分开始时间 */
    @ApiModelProperty(value = "拆分开始时间", required = true)
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private String splitStartTime;

    /** 拆分结束时间 */
    @ApiModelProperty(value = "拆分结束时间", required = true)
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private String splitEndTime;

    /** 助手id */
    @ApiModelProperty(value = "助手id", required = true)
    private Integer assistantId;

}
