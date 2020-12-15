package com.yunya.feign.employee_attend.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * 简介：请假信息查询参数模型
 *
 * @author: chenlin
 * @Description: 请假信息查询参数模型
 * @Date: 2020/11/13 09:42
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("请假信息查询参数模型")
public class LeaveInfoQueryForm implements Serializable {

    @ApiModelProperty("是否分页,默认true")
    private Boolean whetherPage = true;

    @ApiModelProperty("页码，默认第一页")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示条数，默认10条")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    /** 组织id */
    @ApiModelProperty(value = "组织id")
    private Integer orgId;

    /** 员工id */
    @ApiModelProperty(value = "员工id")
    private Integer userId;

    /** 假期id */
    @ApiModelProperty(value = "假期id")
    private Integer vacationId;

    /** 开始时间 */
    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    /** 结束时间 */
    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    /** 开始日期范围 */
    @ApiModelProperty(value = "开始日期范围")
    private Date betweenStartDate;

    /** 结束日期范围 */
    @ApiModelProperty(value = "结束日期范围")
    private Date andStartDate;

    /** 审批条件表id */
    @ApiModelProperty(value = "审批条件表id")
    private Integer approvalCriteriaId;

    /** 审批状态 0 审批中 1通过 2拒绝 3撤回 4异常 */
    @ApiModelProperty(value = "审批状态 0 审批中 1通过 2拒绝 3撤回 4异常")
    private Integer approvalStatus;

    /** 请假方式：0-按班次请假，1-按天请假*/
    @ApiModelProperty(value = "请假方式：0-按班次请假，1-按天请假")
    private Integer vacationStatus;

    /** 主键id列表*/
    @ApiModelProperty(value = "主键id列表")
    private Collection<Integer> ids;
}
