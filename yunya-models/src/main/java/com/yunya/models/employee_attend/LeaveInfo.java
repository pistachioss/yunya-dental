package com.yunya.models.employee_attend;

import lombok.Data;

import java.util.Date;
import javax.persistence.*;
@Data
@Table(name = "leave_info")
public class LeaveInfo {
    @Id
    private Integer id;
    @Column(name = "user_id")
    private Integer userId;
    /**
     * 假期类型id
     */
    @Column(name = "vacation_id")
    private Integer vacationId;

    /**
     * 开始时间
     */
    @Column(name = "start_time")
    private Date startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time")
    private Date endTime;

    /**
     * 请假原因
     */
    @Column(name = "leave_reason")
    private String leaveReason;

    /**
     * 图片（用逗号隔开）
     */
    @Column(name = "leave_picture")
    private String leavePicture;

    /**
     * 审批条件表id
     */
    @Column(name = "approval_criteria_id")
    private Integer approvalCriteriaId;

    /**
     * 审批状态 0 审批中 1通过 2拒绝 3撤回
     */
    @Column(name = "apprpval_status")
    private String apprpvalStatus;

    /**
     * 创建人
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

}