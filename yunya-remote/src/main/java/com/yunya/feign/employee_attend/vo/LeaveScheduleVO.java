package com.yunya.feign.employee_attend.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/11 10:46
 * @since: 1.0.0
 */
public class LeaveScheduleVO implements Serializable {

    /** 主键id */
    @ApiModelProperty(value = "主键id")
    private Integer id;
    
    /** 请假id */
    @ApiModelProperty(value = "请假id")
    private Integer leaveId;

    /** 排班id */
    @ApiModelProperty(value = "'排班id'")
    private Integer scheduleId;
}
