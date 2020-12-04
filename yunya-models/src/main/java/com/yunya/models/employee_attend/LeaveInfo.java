package com.yunya.models.employee_attend;

import lombok.Data;

import java.util.Date;
import javax.persistence.*;
@Data
@Table(name = "leave_info")
public class LeaveInfo {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "company_id")
    private Integer companyId;
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
     * 当前审批人Id（上一次审批人的下一层级） 为了统计待我审批方便
     */
    @Column(name = "approval_now_people_id")
    private Integer approvalNowPeopleId;

    /**
     * 审批条件表id
     */
    @Column(name = "approval_criteria_id")
    private Integer approvalCriteriaId;
    /**
     * 拒绝原因
     */
    @Column(name = "refuse_reason")
    private String refuseReason;
    /**
     * 审批状态 0 审批中 1通过 2拒绝 3撤回
     */
    @Column(name = "apprpval_status")
    private Integer apprpvalStatus;

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