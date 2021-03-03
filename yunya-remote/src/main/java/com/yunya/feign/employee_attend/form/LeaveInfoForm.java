package com.yunya.feign.employee_attend.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.models.employee_attend.ApprovalInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
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
public class LeaveInfoForm {
    @Id
    private Integer id;
    @ApiModelProperty("user_id")
    private Integer userId;

    @ApiModelProperty("用户ID集合")
    private List<Integer> userIds;

    @ApiModelProperty("最高层级审批级别的审批人ID")
    private Integer approvalNowPeopleId;

    @ApiModelProperty("抄送人ID")
    private Integer copyId;

    @ApiModelProperty("门诊ID")
    private Integer companyId;
    /**
     * 假期类型id
     */
    @ApiModelProperty("假期类型id")
    private Integer vacationId;
    /**
     * 假期类型id
     */
    @ApiModelProperty("假期类型 0：班次 1：天")
    private Integer vacationStatus;
    /**
     * 开始时间
     */
    @ApiModelProperty("开始时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endTime;

    /**
     * 请假原因
     */
    @ApiModelProperty("请假原因")
    private String leaveReason;

    /**
     * 图片（用逗号隔开）
     */
    @ApiModelProperty("图片（用逗号隔开）")
    private String leavePicture;

    /**
     * 审批状态 0 审批中 1通过 2拒绝 3撤回
     */
    @ApiModelProperty("审批状态 0 审批中 1通过 2拒绝 3撤回")
    private Integer approvalStatus;

    @ApiModelProperty(value = "拒绝原因")
    private String refuseReason;
    /**
     * 审批人信息列表
     */
    @ApiModelProperty("审批人信息列表")
    private List<ApprovalInfo> approvalPeopleList;

    @ApiModelProperty("抄送人Id集合")
    private List<Integer> copyList;

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
