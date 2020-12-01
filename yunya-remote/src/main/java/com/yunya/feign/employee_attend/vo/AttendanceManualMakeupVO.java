package com.yunya.feign.employee_attend.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * 简介：考勤手动补入时长响应模型
 *
 * @author: chenlin
 * @Description: 考勤手动补入时长响应模型
 * @Date: 2020/12/01 10:21
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("考勤手动补入时长响应模型")
public class AttendanceManualMakeupVO {

    /** 主键id */
    @ApiModelProperty(value = "主键id")
    private Integer id;

    /** 日期 */
    @ApiModelProperty(value = "日期")
    private Date makeupDate;

    /** 员工id */
    @ApiModelProperty(value = "员工id")
    private Integer userId;

    /** 组织id */
    @ApiModelProperty(value = "组织id")
    private Integer orgId;

    /** 手动补入时长（分钟） */
    @ApiModelProperty(value = "手动补入时长（分钟）")
    private Integer minute;


    /** 补入说明（手动输入） */
    @ApiModelProperty(value = "补入说明（手动输入）")
    private String makeupDesc;

    /** 补入类型：0-工作日补入，1-加班补入 */
    @ApiModelProperty(value = "补入类型：0-工作日补入，1-加班补入")
    private Byte type;
}