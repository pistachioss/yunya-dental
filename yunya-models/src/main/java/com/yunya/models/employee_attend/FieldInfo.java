package com.yunya.models.employee_attend;

import lombok.Data;

import java.util.Date;
import javax.persistence.*;
@Data
@Table(name = "field_info")
public class FieldInfo {
    @Id
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;
    /**
     * 外勤地址
     */
    @Column(name = "field_address")
    private String fieldAddress;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

    /**
     * 外勤原因
     */
    @Column(name = "field_reason")
    private String fieldReason;

    /**
     * 审批人id （直接存员工id 与审批人员表无关）
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