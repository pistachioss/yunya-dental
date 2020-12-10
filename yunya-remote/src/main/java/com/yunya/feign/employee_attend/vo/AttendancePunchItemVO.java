package com.yunya.feign.employee_attend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介：考勤打卡项目响应模型
 *
 * @author: chenlin
 * @Description: 考勤打卡项目响应模型
 * @Date: 2020/11/9 15:23
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("考勤打卡项目响应模型")
public class AttendancePunchItemVO implements Serializable {

    /** 打卡项目id */
    @ApiModelProperty(value = "打卡项目id")
    private Integer id;

    @ApiModelProperty(value = "组织名称")
    private String orgName;

    /** 打卡项目名称 */
    @ApiModelProperty(value = "打卡项目名称")
    private String itemName;

    /** 打卡项目开始时间 */
    @ApiModelProperty("打卡项目开始时间")
    @JsonFormat(timezone = "GMT+8", pattern = "HH:mm")
    private Date startTime;

    /** 打卡项目结束时间 */
    @ApiModelProperty("打卡项目结束时间")
    @JsonFormat(timezone = "GMT+8", pattern = "HH:mm")
    private Date endTime;

    /** 是否是即将打卡的项目: false-否，true-是 */
    @ApiModelProperty(value = "是否是即将打卡的项目: false-否，true-是")
    private Boolean isNext;

    /** 打卡的类型: 0-上班，1-下班 */
    @ApiModelProperty(value = "打卡的类型: 0-上班，1-下班")
    private Byte punchType;

    /** 打卡时间 */
    @ApiModelProperty("打卡时间")
    @JsonFormat(timezone = "GMT+8", pattern = "HH:mm")
    private Date punchTime;

    /** 打卡地址或者打卡Wifi名称 */
    @ApiModelProperty(value = "打卡地址或者打卡Wifi名称")
    private String punchName;

    /** 打卡方式: 0-地址打卡，1-Wifi打卡 */
    @ApiModelProperty(value = "打卡方式: 0-地址打卡，1-Wifi打卡")
    private Byte punchMode;

    /** 打卡结果: 0:上班正常 1：迟到  2:下班正常  3:早退  4:无效卡 5:缺卡*/
    @ApiModelProperty(value = "打卡状态: 0:上班正常 1：迟到  2:下班正常  3:早退  4:无效卡 5:缺卡")
    private Byte punchStatus;

    /** 情况: 0:上班 1：休息  2:请假  4:加班 5：外勤 */
    @ApiModelProperty(value = "情况: 0:上班 1：休息  2:请假  4:加班 5：外勤")
    private Byte condition;
}
