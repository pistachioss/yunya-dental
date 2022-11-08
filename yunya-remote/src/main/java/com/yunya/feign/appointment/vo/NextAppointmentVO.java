package com.yunya.feign.appointment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2022/11/7 14:27
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("下次预约数据模型")
public class NextAppointmentVO implements Serializable {
    /** 门诊id */
    @ApiModelProperty("门诊id")
    private Integer orgId;

    /** 门诊简称 */
    @ApiModelProperty("门诊简称")
    private String abbreviation;

    /** 挂号医生id */
    @ApiModelProperty("挂号医生id")
    private Integer dentistId;

    /** 挂号医生姓名 */
    @ApiModelProperty("挂号医生姓名")
    private String dentistName;

    /** 预约时间 */
    @ApiModelProperty("预约时间")
    private String appointDate;
}
