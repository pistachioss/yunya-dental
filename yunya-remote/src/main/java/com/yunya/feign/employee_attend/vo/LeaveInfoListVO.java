package com.yunya.feign.employee_attend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.models.employee_attend.ApprovalInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
@ApiModel(value = "请假信息")
public class LeaveInfoListVO {
    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private Integer id;

    /**
     * 申请人id
     */
    @ApiModelProperty(value = "申请人id")
    private Integer userId;


    @ApiModelProperty(value = "假期类型 0：班次 1：天")
    private Integer vacationStatus;
    /**
     * 申请人
     */
    @ApiModelProperty(value = "申请人")
    private String userName;

    /**
     * 组织id
     */
    @ApiModelProperty(value = "组织id")
    private Integer companyId;

    /**
     * 假期类型id
     */
    @ApiModelProperty(value = "假期类型id")
    private Integer vacationId;

    /**
     * 假期类型
     */
    @ApiModelProperty(value = "假期类型")
    private String vacationName;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date endTime;

    /**
     * 请假原因
     */
    @ApiModelProperty(value = "请假原因")
    private String leaveReason;

    @ApiModelProperty(value = "拒绝原因")
    private String refuseReason;

    /**
     * 图片（用逗号隔开）
     */
    @ApiModelProperty(value = "图片（用逗号隔开）")
    private String leavePicture;

    /**
     * 审批状态 0 审批中 1通过 2拒绝 3撤回
     */
    @ApiModelProperty(value = "审批状态 0 审批中 1通过 2拒绝 3撤回")
    private Integer approvalStatus;
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date crtTime;
    @ApiModelProperty("请假时长(分钟)")
    private Integer leaveTime;

}
