package com.yunya.feign.employee_attend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介：考勤工作时长响应模型
 *
 * @author: chenlin
 * @Description: 考勤工作时长响应模型
 * @Date: 2020/11/17 17:21
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("考勤工作时长响应模型")
public class AttendanceWorkDateMinuteVO implements Serializable {
    /** 日期 */
    @Excel(name = "日期")
    @ApiModelProperty("日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date date;

    /** 排班*/
    @Excel(name = "排班")
    @ApiModelProperty(value = "排班")
    private String employeeScheduleName;

    /** 打上班卡时间 */
    @Excel(name = "打上班卡时间")
    @ApiModelProperty("打上班卡时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date onPunchTime;

    /** 打下班卡时间 */
    @Excel(name = "打下班卡时间")
    @ApiModelProperty("打下班卡时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date offPunchTime;

    /** 请假/外勤/加班：0-无，1-请假，2-加班，3-外勤*/
    @Excel(name = "请假/外勤/加班", readConverterExp ="0=--,1=请假,2=加班,3=外勤")
    @ApiModelProperty(value = "请假/外勤/加班：0-无，1-请假，2-加班，3-外勤")
    private Byte hasApply;

    /** 打卡结果 */
    @Excel(name = "打卡结果")
    @ApiModelProperty(value = "打卡结果")
    private String punchResult;

    /** 工作时长（分钟） */
    @Excel(name = "工作时长（分钟）")
    @ApiModelProperty(value = "工作时长（分钟）")
    private Long minutes;

    /** 补入工作时长（手动输入）*/
    @Excel(name = "补入工作时长（手动输入）")
    @ApiModelProperty(value = "补入工作时长（手动输入）")
    private Long makeupMinutes;

    /** 补入时长说明（手动输入） */
    @Excel(name = "补入时长说明（手动输入）")
    @ApiModelProperty(value = "补入时长说明（手动输入）")
    private String makeupDesc;

    /** 打卡结果是否存在异常：false-否，true-是 */
    @ApiModelProperty(value = "打卡结果是否存在异常：false-否，true-是")
    private Boolean hasException;
}