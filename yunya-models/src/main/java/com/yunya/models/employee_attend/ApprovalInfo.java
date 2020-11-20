package com.yunya.models.employee_attend;

import lombok.Data;

import java.util.Date;
import javax.persistence.*;

@Table(name = "approval_info")
@Data
public class ApprovalInfo {
    @Id
    private Integer id;

    /**
     * 请假信息表id
     */
    @Column(name = "leave_id")
    private Integer leaveId;

    /**
     * 审批级别人员Id
     */
    @Column(name = "approval_people_id")
    private Integer approvalPeopleId;

    /**
     * 此审批人给予当前请假的审批状态
     */
    @Column(name = "approval_status")
    private Integer approvalStatus = 0;

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