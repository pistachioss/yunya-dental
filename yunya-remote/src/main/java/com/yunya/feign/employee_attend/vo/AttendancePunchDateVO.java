package com.yunya.feign.employee_attend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介：考勤打卡记录日期情况响应模型
 *
 * @author: chenlin
 * @Description: 考勤打卡记录日期情况响应模型
 * @Date: 2020/11/13 15:21
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("考勤打卡记录日期情况响应模型")
public class AttendancePunchDateVO implements Serializable {

    /** 打卡日期 */
    @ApiModelProperty(value = "打卡日期")
    private Date punchDate;

    /** 员工id */
    @ApiModelProperty(value = "员工id")
    private Integer userId;

    /** 上班打卡状态：0-正常打卡、1-迟到、4-无效卡、5-未打卡 */
    @ApiModelProperty(value = "上班打卡状态：0-正常打卡、1-迟到、4-无效卡、5-未打卡")
    private Byte onDutyStatus;

    /** 上班打卡组织id */
    @ApiModelProperty(value = "上班打卡组织id")
    private Integer onDutyOrgId;

    /** 上班结束时间 */
    @ApiModelProperty(value = "上班结束时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date onDutyEndTime;

    /** 上班班次名称（或者请假、或者外勤、或者加班） */
    @ApiModelProperty(value = "上班班次名称（或者请假、或者外勤、或者加班）")
    private String onDutyName;

    /** 上班打卡时间 */
    @ApiModelProperty("打卡时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date onDutyPunchTime;

    /** 下班打卡状态：2-正常打卡、3-迟到、4-无效卡、5-未打卡 */
    @ApiModelProperty(value = "下班打卡状态：2-正常打卡、3-迟到、4-无效卡、5-未打卡")
    private Byte offDutyStatus;

    /** 下班打卡组织id */
    @ApiModelProperty(value = "下班打卡组织id")
    private Integer offDutyOrgId;

    /** 下班开始时间 */
    @ApiModelProperty(value = "下班开始时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date offDutyStartTime;

    /** 下班结束时间 */
    @ApiModelProperty(value = "下班结束时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date offDutyEndTime;

    /** 下班打卡时间 */
    @ApiModelProperty("下班打卡时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date offDutyPunchTime;

    /** 下班班次名称（或者请假、或者外勤、或者加班） */
    @ApiModelProperty(value = "下班班次名称（或者请假、或者外勤、或者加班） ")
    private String offDutyName;

    /** 请假情况：0-上午请假，1-下午请假，2-全天请假 */
    @ApiModelProperty(value = "请假情况：0-上午请假，1-下午请假，2-全天请假")
    private Byte leave;

    /** 加班情况：0-上午加班，1-下午加班，2-全天加班 */
    @ApiModelProperty(value = "加班情况：0-上午加班，1-下午加班，2-全天加班")
    private Byte workOvertime;

    /** 外勤情况：0-上午外勤，1-下午外勤，2-全天外勤 */
    @ApiModelProperty(value = "外勤情况：0-上午外勤，1-下午外勤，2-全天外勤")
    private Byte field;
}
