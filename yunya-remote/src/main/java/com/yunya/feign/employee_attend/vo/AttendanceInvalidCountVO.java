package com.yunya.feign.employee_attend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介：考勤无效卡次数统计响应模型
 *
 * @author: chenlin
 * @Description: 考勤无效卡次数统计响应模型
 * @Date: 2020/12/03 15:21
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("考勤无效卡次数统计响应模型")
public class AttendanceInvalidCountVO implements Serializable {
    private static final long serialVersionUID = -5761667585479606048L;
    /** 日期 */
    @ApiModelProperty("日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date date;

    /** 排班 */
    @ApiModelProperty(value = "排班")
    private String employeeScheduleName;

    /** 打上班卡时间 */
    @ApiModelProperty("打上班卡时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date onPunchTime;

    /** 打下班卡时间 */
    @ApiModelProperty("打下班卡时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date offPunchTime;

    /** 缺卡次数 */
    @ApiModelProperty(value = "早退时长（分钟）")
    private Integer count;

    /** 打卡结果 */
    @ApiModelProperty(value = "打卡结果")
    private String punchResult;

    /** 打卡地址/WIFI */
    @ApiModelProperty(value = "打卡地址/WIFI")
    private String punchAddress;
}
