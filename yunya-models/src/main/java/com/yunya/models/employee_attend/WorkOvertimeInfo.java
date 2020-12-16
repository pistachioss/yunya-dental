package com.yunya.models.employee_attend;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
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
     * 加班班次id
     */
    @Column(name = "schedule_id")
    private Integer scheduleId;

    /**
     * 要加班的休息班次id
     */
    @Column(name = "rest_schedule_id")
    private Integer restScheduleId;
    /**
     * 加班日期
     */
    @Column(name = "work_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date workDate;

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
    @Column(name = "approval_status")
    private Integer approvalStatus;
    /**
     * 拒绝原因
     */
    @Column(name = "refuse_reason")
    private String refuseReason;

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