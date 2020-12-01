package com.yunya.feign.employee_attend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介：考勤工作日加班时长响应模型
 *
 * @author: chenlin
 * @Description: 考勤工作日加班时长响应模型
 * @Date: 2020/11/17 17:21
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("考勤工作日加班时长响应模型")
public class AttendanceWorkDateOvertimeMinuteVO implements Serializable {
    /** 日期 */
    @ApiModelProperty("日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date date;

    /** 排班*/
    @ApiModelProperty(value = "排班")
    private String employeeScheduleName;

    /** 下班卡考勤时间 */
    @ApiModelProperty("下班卡考勤时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date endTime;

    /** 打下班卡时间 */
    @ApiModelProperty("打下班卡时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date punchTime;

    /** 工作时长（分钟） */
    @ApiModelProperty(value = "工作时长（分钟）")
    private Long minutes;

    /** 打卡地点/WIFI */
    @ApiModelProperty(value = "打卡地点/WIFI")
    private String punchAddress;
}