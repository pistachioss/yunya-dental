package com.yunya.feign.employee_attend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介：考勤外勤时长统计响应模型
 *
 * @author: chenlin
 * @Description: 考勤外勤时长统计响应模型
 * @Date: 2020/11/30 17:21
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("考勤外勤时长统计响应模型")
public class AttendanceFieldMinuteVO implements Serializable {

    /** 打上班卡时间 */
    @ApiModelProperty("打上班卡时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date onPunchTime;

    /** 打下班卡时间 */
    @ApiModelProperty("打下班卡时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date offPunchTime;

    /** 外勤打卡地点 */
    @ApiModelProperty("外勤打卡地点")
    private String punchAddresss;

    /** 排班 */
    @ApiModelProperty(value = "排班")
    private String employeeScheduleName;

    /** 外勤时长(分钟) */
    @ApiModelProperty(value = "外勤时长(分钟)")
    private Long minutes;

    /** 申请日期 */
    @ApiModelProperty(value = "申请日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date applyDate;

    /** 外勤日期 */
    @ApiModelProperty(value = "外勤日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date fieldDate;

    /** 申请外勤开始时间 */
    @ApiModelProperty(value = "申请外勤开始时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date startTime;

    /** 申请外勤结束时间 */
    @ApiModelProperty(value = "申请外勤结束时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date endTime;

    /** 申请外勤地点 */
    @ApiModelProperty(value = "申请外勤结束时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private String fieldAddress;

    /** 审批人 */
    @ApiModelProperty(value = "审批人")
    private String approvalUserName;
}