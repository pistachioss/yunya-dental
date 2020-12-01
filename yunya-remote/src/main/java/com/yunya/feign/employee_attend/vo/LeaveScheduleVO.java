package com.yunya.feign.employee_attend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介：请假时间段内排班关联响应模型
 *
 * @author: chenlin
 * @Description: 请假时间段内排班关联响应模型
 * @Date: 2020/11/11 10:46
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("请假时间段内排班关联响应模型")
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

    /** 开始时间 */
    @ApiModelProperty(value = "'开始时间'")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date startTime;

    /** 结束时间 */
    @ApiModelProperty(value = "'结束时间'")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date endTime;

    /** 审批用户id */
    @ApiModelProperty(value = "审批用户id")
    private Integer approvalPeopleId;
}
