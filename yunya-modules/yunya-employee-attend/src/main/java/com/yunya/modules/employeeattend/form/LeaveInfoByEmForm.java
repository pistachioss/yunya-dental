package com.yunya.modules.employeeattend.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.models.employee_attend.ApprovalInfo;
import com.yunya.models.employee_attend.LeaveSchedule;
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
public class LeaveInfoByEmForm {
    @Id
    private Integer id;

    @ApiModelProperty("用户id")
    private Integer userId;

    @ApiModelProperty("最高层级审批级别的审批人ID")
    private Integer approvalNowPeopleId;

    @ApiModelProperty("门诊ID")
    private Integer companyId;
    /**
     * 假期类型id
     */
    @ApiModelProperty("假期类型id")
    private Integer vacationId;

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
     * 班次请假信息列表
     */
    @ApiModelProperty("班次请假信息列表")
    private List<LeaveSchedule> scList;

    /**
     * 图片（用逗号隔开）
     */
    @ApiModelProperty("图片（用逗号隔开）")
    private String leavePicture;

    /**
     * 审批状态 0 审批中 1通过 2拒绝 3撤回
     */
    @ApiModelProperty("审批状态 0 审批中 1通过 2拒绝 3撤回")
    private Integer apprpvalStatus = 0;
    /**
     * 审批人信息列表
     */
    @ApiModelProperty("审批人信息列表")
    private List<ApprovalInfo> apprpvalPeopleList;

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
