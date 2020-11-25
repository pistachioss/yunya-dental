package com.yunya.feign.employee_attend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介：考勤打卡时长统计响应模型
 *
 * @author: chenlin
 * @Description: 考勤打卡时长统计响应模型
 * @Date: 2020/11/17 17:21
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("考勤打卡时长统计响应模型")
public class AttendancePunchMinuteVO implements Serializable {
    /** 时间 */
    @ApiModelProperty("时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date date;

    /** 班次名称*/
    @ApiModelProperty(value = "班次名称")
    private String name;

    /** 考勤时间 */
    @ApiModelProperty("考勤时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date attendanceTime;

    /** 打卡时间 */
    @ApiModelProperty("打卡时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date punchTime;

    /** 是否请假或外勤：0-无，1-请假，2外勤*/
    @ApiModelProperty(value = "是否请假或外勤：0-无，1-请假，2外勤")
    private Byte isLeaveOrField;

    /** 打卡结果: 0-上班正常，1-迟到，2-下班正常，3-早退，4-无效卡，5-异常，6-正常 */
    @ApiModelProperty(value = "打卡结果: 0-上班正常，1-迟到，2-下班正常，3-早退，4-无效卡，5-异常，6-正常")
    private Byte punchStatus;

    /** 工作或者加班或者请假或者外勤时长/分钟 */
    @ApiModelProperty(value = "工作或者加班或者请假或者外勤时长/分钟")
    private Long minutes;

    /** 考勤地址或者打卡地址/wifi */
    @ApiModelProperty(value = "考勤地址或者打卡地址/wifi")
    private String punchAddress;

    /** 补入时长/分钟 */
    @ApiModelProperty(value = "补入时长/分钟")
    private Long interpolationMinutes;

    /** 补入时长说明 */
    @ApiModelProperty(value = "补入时长说明")
    private String interpolation;

    /** 申请时间 */
    @ApiModelProperty(value = "申请时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date applyDate;

    /** 申请地址 */
    @ApiModelProperty(value = "申请地址")
    private String applyAddress;

    /** 申请班次 */
    @ApiModelProperty(value = "申请班次")
    private String applySchedule;

    /** 审批人 */
    @ApiModelProperty(value = "审批人")
    private String approvalUserName;

    /** 审批状态 */
    @ApiModelProperty(value = "审批状态")
    private Byte approvalStatus;

    /** 请假类型 */
    @ApiModelProperty(value = "请假类型")
    private Byte vacationName;

    /** 加班日期/请假时间/外勤时间 */
    @ApiModelProperty(value = "加班日期/请假时间/外勤时间")
    private String approvalTime;
}