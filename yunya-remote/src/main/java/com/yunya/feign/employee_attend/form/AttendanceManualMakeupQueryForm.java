package com.yunya.feign.employee_attend.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.Date;

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
public class AttendanceManualMakeupQueryForm implements Serializable {
    @ApiModelProperty("是否分页,默认true")
    private Boolean whetherPage = true;

    @ApiModelProperty("页码，默认第一页")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示条数，默认10条")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    /** 员工id */
    @ApiModelProperty(value = "员工id")
    private Integer userId;

    /** 组织id */
    @ApiModelProperty(value = "组织id")
    private Integer orgId;

    /** 日期 */
    @ApiModelProperty(value = "日期")
    private Date makeupDate;

    /** 开始日期 */
    @ApiModelProperty(value = "开始日期")
    private Date betweenDate;

    /** 结束日期 */
    @ApiModelProperty(value = "结束日期")
    private Date andDate;

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
