package com.yunya.feign.employee_attend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介：考勤打卡记录响应模型
 *
 * @author: chenlin
 * @Description: 考勤打卡记录响应模型
 * @Date: 2020/11/9 15:21
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("考勤打卡记录响应模型")
public class AttendancePunchRecordVO implements Serializable {

    /** 主键id */
    @ApiModelProperty(value = "主键id")
    private Integer id;

    /** 员工id */
    @ApiModelProperty(value = "员工id")
    private Integer userId;

    /** 组织id */
    @ApiModelProperty(value = "组织id")
    private Integer orgId;

    /** 组织名称 */
    @ApiModelProperty(value = "组织名称")
    private String orgName;

    /** 班次名称（或者请假、或者外勤、或者加班） */
    @ApiModelProperty(value = "班次名称（或者请假、或者外勤、或者加班）")
    private String name;

    /** 打卡日期 */
    @ApiModelProperty("打卡日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date punchDate;

    /** 打卡时间 */
    @ApiModelProperty("打卡时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date punchTime;

    /** 开始时间 */
    @ApiModelProperty("开始时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date startTime;

    /** 结束时间 */
    @ApiModelProperty("结束时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date endTime;

    /** 打卡的类型: 0-上班，1-下班 */
    @ApiModelProperty(value = "打卡的类型: 0-上班，1-下班")
    private Byte punchType;

    /** 打卡状态: 0:上班正常 1：迟到  2:下班正常  3:早退  4:无效卡 */
    @ApiModelProperty(value = "打卡状态: 0:上班正常 1：迟到  2:下班正常  3:早退  4:无效卡")
    private Byte punchStatus;

    /** 打卡项目来源: 0:班次 1：请假  2：加班 3：外勤 */
    @ApiModelProperty(value = "打卡项目来源: 0:班次 1：请假  2：加班 3：外勤")
    private Byte source;

    /** 考勤地址Id */
    @ApiModelProperty(value = "考勤地址Id")
    private Integer attendanceAddressId;

    /** 打卡经度 */
    @ApiModelProperty(value = "打卡经度")
    private Integer longitude;

    /** 打卡纬度 */
    @ApiModelProperty(value = "打卡纬度")
    private Integer latitude;

    /** 打卡地址（或者wifi名称） */
    @ApiModelProperty(value = "打卡地址（或者wifi名称）")
    private String punchAddress;

    /** 打卡项目来源id：员工排班id、请假id、加班id、外勤id */
    @ApiModelProperty(value = "打卡项目来源id：员工排班id、请假id、加班id、外勤id")
    private Integer sourceId;

    /** 是否打卡：0-否，1-是 */
    @ApiModelProperty(value = "是否打卡：0-否，1-是")
    private Byte isPunch;

    /** wifi的mac地址 */
    @ApiModelProperty(value = "wifi的mac地址")
    private String wifiMacAddress;

    /** 排班班次id */
    @ApiModelProperty(value = "排班班次id")
    private Integer scheduleId;

    /** 时长（分钟） */
    @ApiModelProperty(value = "时长（分钟）")
    private Long minutes;
}
