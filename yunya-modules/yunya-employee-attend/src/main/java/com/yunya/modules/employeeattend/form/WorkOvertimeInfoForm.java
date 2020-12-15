package com.yunya.modules.employeeattend.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

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
public class WorkOvertimeInfoForm {

    private Integer id;

    @ApiModelProperty("申请人ID")
    private Integer userId;

    @ApiModelProperty("抄送人ID")
    private Integer copyId;

    @ApiModelProperty("申请要加班的日期")
    private Date workDate;

    @ApiModelProperty("开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty("结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date endTime;
    /**
     * 门诊id
     */
    @ApiModelProperty("门诊id")
    private Integer companyId;
    /**
     * 休息班次id
     */
    @ApiModelProperty("休息班次id")
    private Integer restScheduleId;
    /**
     * 班次id
     */
    @ApiModelProperty("加班班次id")
    private Integer scheduleId;

    /**
     * 加班事由
     */
    @ApiModelProperty("加班事由")
    private String overtimeReason;

    /**
     * 审批人id（直接存员工id 与审批人员表没有关系）
     */
    @ApiModelProperty("审批人id（直接存员工id 与审批人员表没有关系）")
    private Integer approvalPeopleId;

    @ApiModelProperty("抄送人Id集合")
    private List<Integer> copyList;

    /**
     * 审批状态 0 审批中 1通过 2拒绝  3撤回
     */
    @ApiModelProperty("审批状态 0 审批中 1通过 2拒绝  3撤回")
    private Integer approvalStatus;

    @ApiModelProperty("拒绝条件")
    private String refuseReason;

    /**
     * 创建人
     */
    @ApiModelProperty("crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @ApiModelProperty("crt_time")
    private Date crtTime;

    /**
     * 更新人
     */
    @ApiModelProperty("upd_id")
    private Integer updId;

    /**
     * 更新时间
     */
    @ApiModelProperty("upd_time")
    private Date updTime;
}
