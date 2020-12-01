package com.yunya.feign.employee_attend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介：考勤休息日加班时长统计响应模型
 *
 * @author: chenlin
 * @Description: 考勤休息日加班时长统计响应模型
 * @Date: 2020/11/17 17:21
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("考勤休息日加班时长统计响应模型")
public class AttendanceLeaveMinuteVO implements Serializable {

    /** 请假开始时间 */
    @ApiModelProperty("请假开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date leaveStartTime;

    /** 请假结束时间 */
    @ApiModelProperty("请假结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date leaveEndTime;

    /** 请假类型*/
    @ApiModelProperty(value = "请假类型")
    private String leaveType;

    /** 请假时长(分钟) */
    @ApiModelProperty(value = "请假时长(分钟)")
    private Long minutes;

    /** 申请日期 */
    @ApiModelProperty(value = "申请日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date applyDate;

    /** 审批人 */
    @ApiModelProperty(value = "审批人")
    private String approvalUserName;
}