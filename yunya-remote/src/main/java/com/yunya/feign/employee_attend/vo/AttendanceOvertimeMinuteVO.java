package com.yunya.feign.employee_attend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介：考勤休息日加班时长统计响应模型
 *
 * @author: chenlin
 * @Description: 考勤休息日加班时长统计响应模型
 * @Date: 2020/11/17 17:21
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("考勤休息日加班时长统计响应模型")
public class AttendanceOvertimeMinuteVO implements Serializable {
    /** 主键id*/
    @ApiModelProperty(value = "主键id")
    private Integer id;

    /** 审批用户id*/
    @ApiModelProperty(value = "审批用户id")
    private Integer approvalPeopleId;

    /** 组织id*/
    @ApiModelProperty(value = "组织id")
    private Integer companyId;

    /** 加班日期 */
    @ApiModelProperty("加班日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date workDate;

    /** 申请加班班次*/
    @ApiModelProperty(value = "申请加班班次")
    private String scheduleName;


    /** 打上班卡时间 */
    @ApiModelProperty("打上班卡时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date onPunchTime;

    /** 打下班卡时间 */
    @ApiModelProperty("打下班卡时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date offPunchTime;

    /** 加班时长(分钟) */
    @ApiModelProperty(value = "加班时长(分钟)")
    private Long minutes;

    /** 排班 */
    @ApiModelProperty(value = "排班")
    private String employeeScheduleName;

    /** 申请日期 */
    @ApiModelProperty(value = "申请日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date applyDate;

    /** 审批人 */
    @ApiModelProperty(value = "审批人")
    private String approvalUserName;

    /** 手动补入时长(分钟) */
    @ApiModelProperty(value = "手动补入时长(分钟)")
    private Integer makeupMinute;

    /** 手动补入说明 */
    @ApiModelProperty(value = "手动补入说明")
    private String makeupDesc;
}