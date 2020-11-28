package com.yunya.feign.employee_attend.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 简介：考勤手动补入时长添加模型
 *
 * @author: chenlin
 * @Description: 考勤手动补入时长添加模型
 * @Date: 2020/11/5 12:18
 * @since: 1.0.0
 */
@ApiModel("考勤手动补入时长添加模型")
@Data
@ToString
public class AttendanceManualMakeupModel implements Serializable {

    private static final long serialVersionUID = 3409176109167469169L;
    /** 员工id */
    @NotNull(message = "员工id不能为空")
    @ApiModelProperty(value = "员工id",required = true)
    private Integer userId;

    /** 组织id */
    @NotNull(message = "组织id不能为空")
    @ApiModelProperty(value = "组织id",required = true)
    private Integer orgId;

    /** 补入日期 */
    @NotNull(message = "日期不能为空")
    @ApiModelProperty(value = "补入日期",required = true)
    private Date makeupDate;

    /** 手动补入时长（分钟） */
    @ApiModelProperty(value = "手动补入时长（分钟）")
    private Integer minute;

    /** 补入说明（手动输入） */
    @ApiModelProperty(value = "补入说明（手动输入）")
    private String makeupDesc;

    /** 补入类型：0-工作日补入，1-加班补入 */
    @NotNull(message = "补入类型不能为空")
    @ApiModelProperty(value = "补入类型：0-工作日补入，1-加班补入",required = true)
    private Byte type;
}
