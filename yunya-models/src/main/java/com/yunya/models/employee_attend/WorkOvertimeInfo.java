package com.yunya.models.employee_attend;

import lombok.Data;

import java.util.Date;
import javax.persistence.*;
@Data
@Table(name = "work_overtime_info")
public class WorkOvertimeInfo {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;
    @Column(name = "user_id")
    private Integer userId;
    /**
     * 门诊id
     */
    @Column(name = "company_id")
    private Integer companyId;

    /**
     * 班次id
     */
    @Column(name = "schedule_id")
    private Integer scheduleId;


    /**
     * 加班事由
     */
    @Column(name = "overtime_reason")
    private String overtimeReason;

    /**
     * 审批人id（直接存员工id 与审批人员表没有关系）
     */
    @Column(name = "approval_people_id")
    private Integer approvalPeopleId;

    /**
     * 审批状态 0 审批中 1通过 2拒绝  3撤回 4过期
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