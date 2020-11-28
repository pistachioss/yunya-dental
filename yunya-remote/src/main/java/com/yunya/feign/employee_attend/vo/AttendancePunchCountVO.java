package com.yunya.feign.employee_attend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介：考勤打卡次数统计响应模型
 *
 * @author: chenlin
 * @Description: 考勤打卡次数统计响应模型
 * @Date: 2020/11/17 15:21
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("考勤打卡次数统计响应模型")
public class AttendancePunchCountVO implements Serializable {
    private static final long serialVersionUID = -3781575532862391245L;
    /** 日期 */
    @ApiModelProperty("日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date date;

    /** 排班 */
    @ApiModelProperty(value = "排班")
    private String name;

    /** 上班卡考勤时间 */
    @ApiModelProperty("上班卡考勤时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date onAttendanceTime;

    /** 下班卡考勤时间 */
    @ApiModelProperty("下班卡考勤时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date offAttendanceTime;

    /** 上班卡打卡时间 */
    @ApiModelProperty("上班卡打卡时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date onPunchTime;

    /** 下班卡打卡时间 */
    @ApiModelProperty("下班卡打卡时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date offPunchTime;

    /** 打卡结果 */
    @ApiModelProperty(value = "打卡结果")
    private String punchResult;

    /** 迟到或者早退时长/分钟 */
    @ApiModelProperty(value = "迟到或者早退时长/分钟")
    private Long minutes;

    /** 打卡地址/wifi */
    @ApiModelProperty(value = "打卡地址/wifi")
    private String punchAddress;

    /** 缺卡次数/无效卡次数 */
    @ApiModelProperty(value = "缺卡次数/无效卡次数")
    private Integer count;
}
