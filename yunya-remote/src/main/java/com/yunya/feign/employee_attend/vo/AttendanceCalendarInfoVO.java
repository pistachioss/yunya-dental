package com.yunya.feign.employee_attend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import java.util.Date;

/**
 * 简介：考勤日历信息响应模型
 *
 * @author: chenlin
 * @Description: 考勤信息响应模型
 * @Date: 2020/11/10 15:37
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("考勤日历信息响应模型")
public class AttendanceCalendarInfoVO {

    /** 日期 */
    @ApiModelProperty(value = "日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date date;

    /** 考勤状态：0-正常（正常的上下班），1-异常（迟到、早退、旷工、缺卡、请假、外勤），2-未知（未排班、未开始），3-休息*/
    @ApiModelProperty(value = "考勤状态：0-正常（正常的上下班、休息），1-异常（迟到、早退、旷工、缺卡、请假、外勤），2-未知（未排班、未开始），3-休息")
    private Byte type;

    /** 考勤名称：正常上班、休息、迟到、早退、旷工、缺卡、请假、外勤、未排班、未开始 */
    @ApiModelProperty(value = "考勤名称：正常上班、休息、迟到、早退、旷工、缺卡、请假、外勤、未排班、未开始")
    private String name;
}
