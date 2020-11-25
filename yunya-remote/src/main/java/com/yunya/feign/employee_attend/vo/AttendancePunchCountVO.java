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

    /** 打卡结果: 0-上班缺卡，1-迟到，2-下班缺卡，3-早退，4-无效卡，5-异常 */
    @ApiModelProperty(value = "打卡结果: 0-上班缺卡，1-迟到，2-下班缺卡，3-早退，4-无效卡，5-异常")
    private Byte punchStatus;

    /** 迟到或者早退时长/分钟 */
    @ApiModelProperty(value = "迟到或者早退时长/分钟")
    private Long minutes;

    /** 打卡地址/wifi */
    @ApiModelProperty(value = "打卡地址/wifi")
    private String punchAddress;
}
