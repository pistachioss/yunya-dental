package com.yunya.models.employee_attend;

import java.util.Date;
import javax.persistence.*;

@Table(name = "approval_info")
public class ApprovalInfo {
    @Id
    private Integer id;

    /**
     * 请假信息表id
     */
    @Column(name = "vacation_id")
    private Integer vacationId;

    /**
     * 审批级别人员Id
     */
    @Column(name = "approval_people_id")
    private Integer approvalPeopleId;

    /**
     * 此审批人给予当前请假的审批状态
     */
    @Column(name = "approval_status")
    private Integer approvalStatus;

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

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取请假信息表id
     *
     * @return vacation_id - 请假信息表id
     */
    public Integer getVacationId() {
        return vacationId;
    }

    /**
     * 设置请假信息表id
     *
     * @param vacationId 请假信息表id
     */
    public void setVacationId(Integer vacationId) {
        this.vacationId = vacationId;
    }

    /**
     * 获取审批级别人员Id
     *
     * @return approval_people_id - 审批级别人员Id
     */
    public Integer getApprovalPeopleId() {
        return approvalPeopleId;
    }

    /**
     * 设置审批级别人员Id
     *
     * @param approvalPeopleId 审批级别人员Id
     */
    public void setApprovalPeopleId(Integer approvalPeopleId) {
        this.approvalPeopleId = approvalPeopleId;
    }

    /**
     * 获取此审批人给予当前请假的审批状态
     *
     * @return approval_status - 此审批人给予当前请假的审批状态
     */
    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    /**
     * 设置此审批人给予当前请假的审批状态
     *
     * @param approvalStatus 此审批人给予当前请假的审批状态
     */
    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    /**
     * 获取创建人
     *
     * @return crt_id - 创建人
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置创建人
     *
     * @param crtId 创建人
     */
    public void setCrtId(Integer crtId) {
        this.crtId = crtId;
    }

    /**
     * 获取创建时间
     *
     * @return crt_time - 创建时间
     */
    public Date getCrtTime() {
        return crtTime;
    }

    /**
     * 设置创建时间
     *
     * @param crtTime 创建时间
     */
    public void setCrtTime(Date crtTime) {
        this.crtTime = crtTime;
    }

    /**
     * 获取更新人
     *
     * @return upd_id - 更新人
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * 设置更新人
     *
     * @param updId 更新人
     */
    public void setUpdId(Integer updId) {
        this.updId = updId;
    }

    /**
     * 获取更新时间
     *
     * @return upd_time - 更新时间
     */
    public Date getUpdTime() {
        return updTime;
    }

    /**
     * 设置更新时间
     *
     * @param updTime 更新时间
     */
    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }
}