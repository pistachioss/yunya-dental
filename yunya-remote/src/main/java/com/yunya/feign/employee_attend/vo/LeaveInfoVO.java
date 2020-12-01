package com.yunya.feign.employee_attend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介：请假信息响应模型
 *
 * @author: chenlin
 * @Description: 请假信息响应模型
 * @Date: 2020/11/11 10:37
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("请假信息响应模型")
public class LeaveInfoVO implements Serializable {
    /** 主键id */
    @ApiModelProperty(value = "主键id")
    private Integer id;

    /** 申请人id */
    @ApiModelProperty(value = "申请人id")
    private Integer userId;

    /** 组织id */
    @ApiModelProperty(value = "组织id")
    private Integer orgId;

    /**
     * 假期类型id
     */
    @ApiModelProperty(value = "假期类型id")
    private Integer vacationId;


    /**
     * 开始日期或请假日期
     */
    @ApiModelProperty(value = "开始日期或请假日期")
    private Date startDate;


    /**
     * 结束日期
     */
    @ApiModelProperty(value = "结束日期")
    private Date endDate;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    /**
     * 请假原因
     */
    @ApiModelProperty(value = "请假原因")
    private String leaveReason;

    /**
     * 图片（用逗号隔开）
     */
    @ApiModelProperty(value = "图片（用逗号隔开）")
    private String leavePicture;

    /**
     * 审批条件表id
     */
    @ApiModelProperty(value = "审批条件表id")
    private Integer approvalCriteriaId;

    /**
     * 审批状态 0 审批中 1通过 2拒绝 3撤回
     */
    @ApiModelProperty(value = "审批状态 0 审批中 1通过 2拒绝 3撤回")
    private Integer apprpvalStatus;

    /**
     * 申请类型 0：班次 1：天
     */
    @ApiModelProperty(value = "申请类型 0：班次 1：天")
    private Integer vacationStatus;

    /**
     * 申请类型名称
     */
    @ApiModelProperty(value = "申请类型名称")
    private String vacationName;

    /**
     * 员工排班id
     */
    @ApiModelProperty(value = "员工排班id")
    private Integer scheduleId;

    /**
     * 申请日期
     */
    @ApiModelProperty(value = "申请日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date crtTime;
}