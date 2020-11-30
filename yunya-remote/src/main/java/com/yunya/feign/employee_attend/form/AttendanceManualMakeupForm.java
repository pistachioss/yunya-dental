package com.yunya.feign.employee_attend.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介：考勤手动补入时长修改模型
 *
 * @author: chenlin
 * @Description: 考勤手动补入时长修改模型
 * @Date: 2020/11/28 12:18
 * @since: 1.0.0
 */
@ApiModel("考勤手动补入时长修改模型")
@Data
@ToString
public class AttendanceManualMakeupForm implements Serializable {

    private static final long serialVersionUID = 1990691445404097510L;
    /** 主键id */
    @NotNull(message = "主键id不能为空")
    @ApiModelProperty(value = "主键id", required = true)
    private Integer id;

    /** 手动补入时长（分钟） */
    @ApiModelProperty(value = "手动补入时长（分钟）")
    private Integer minute;

    /** 补入说明（手动输入） */
    @ApiModelProperty(value = "补入说明（手动输入）")
    private String makeupDesc;
}
