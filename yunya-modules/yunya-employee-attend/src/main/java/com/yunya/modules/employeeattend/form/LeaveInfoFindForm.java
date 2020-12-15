package com.yunya.modules.employeeattend.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 简介:
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Data
public class LeaveInfoFindForm {
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

    /** 审批状态 0 审批中 1通过 2拒绝 3撤回*/
    @ApiModelProperty(value = "审批状态 0 审批中 1通过 2拒绝 3撤回")
    private Integer approvalStatus;

    /** 请假方式：0-按班次请假，1-按天请假*/
    @ApiModelProperty(value = "请假方式：0-按班次请假，1-按天请假")
    private Integer vacationStatus;
}
