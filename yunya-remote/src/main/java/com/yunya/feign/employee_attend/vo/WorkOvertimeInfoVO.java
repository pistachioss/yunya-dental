package com.yunya.feign.employee_attend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介：加班信息响应模型
 *
 * @author: chenlin
 * @Description: 加班信息响应模型
 * @Date: 2020/11/11 11:09
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("加班信息响应模型")
public class WorkOvertimeInfoVO implements Serializable {
    /** 主键id */
    @ApiModelProperty(value = "主键id")
    private Integer id;

    /** 申请人ID */
    @ApiModelProperty(value = "申请人ID")
    private Integer userId;

    /** 加班日期 */
    @ApiModelProperty(value = "加班日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date workDate;

    /** 开始时间 */
    @ApiModelProperty(value = "开始日期")
    @JsonFormat(pattern = "HH:mm",timezone = "GMT+8")
    private Date startTime;

    /** 结束时间 */
    @ApiModelProperty(value = "结束日期")
    @JsonFormat(pattern = "HH:mm",timezone = "GMT+8")
    private Date endTime;

    /** 门诊id */
    @ApiModelProperty(value = "门诊id")
    private Integer companyId;

    /** 休息班次id */
    @ApiModelProperty(value = "休息班次id")
    private Integer restScheduleId;

    /** 休息班次名称 */
    @ApiModelProperty(value = "休息班次名称")
    private String restScheduleName;

    /** 加班班次id */
    @ApiModelProperty(value = "加班班次id")
    private Integer scheduleId;

    /** 加班事由 */
    @ApiModelProperty(value = "加班事由")
    private String overtimeReason;

    /** 审批人id（直接存员工id 与审批人员表没有关系） */
    @ApiModelProperty(value = "审批人id（直接存员工id 与审批人员表没有关系）")
    private Integer approvalPeopleId;

    /** 审批状态 0 审批中 1通过 2拒绝  3撤回 4过期*/
    @ApiModelProperty(value = "审批状态 0 审批中 1通过 2拒绝  3撤回 4过期")
    private Integer approvalStatus;

    /** 申请时间 */
    @ApiModelProperty(value = "申请时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date crtTime;
}
