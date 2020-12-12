package com.yunya.feign.employee_attend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.framework.common.exception.ClientServiceException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.yunya.framework.common.constant.OperationCodeConstants.DATA_TRANSFORMATION_EXIST;

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

    /** 打卡时间 (带秒钟的) */
    @ApiModelProperty("打卡时间(带秒钟的)")
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private Date originalPunchTime;

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

    /** 考勤项目来源: 0：上班班次； 1：休息班次；2：按天请假； 3：按班次请假；4：加班；5：外勤 */
    @ApiModelProperty(value = "考勤项目来源: 0：上班班次； 1：休息班次；2：按天请假； 3：按班次请假；4：加班；5：外勤")
    private Byte source;

    /** 考勤地址Id */
    @ApiModelProperty(value = "考勤地址Id")
    private Integer attendanceAddressId;

    /** 打卡经度 */
    @ApiModelProperty(value = "打卡经度")
    private String longitude;

    /** 打卡纬度 */
    @ApiModelProperty(value = "打卡纬度")
    private String latitude;

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

    /** 时长（小于60分钟的只显示分钟，否则显示xx小时xx分钟） */
    @ApiModelProperty(value = "时长（分钟）")
    private String minutes;

    public Date getPunchTime() {
        if (punchTime == null) {
            return punchTime;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String timeStr = sdf.format(punchTime);
        Date time;
        try {
            time = sdf.parse(timeStr);
        } catch (ParseException e) {
            throw new ClientServiceException("时间转换错误", DATA_TRANSFORMATION_EXIST);
        }
        return time;
    }
}
