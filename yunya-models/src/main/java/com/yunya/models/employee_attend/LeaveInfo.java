package com.yunya.models.employee_attend;

import java.util.Date;
import javax.persistence.*;

@Table(name = "leave_info")
public class LeaveInfo {
    @Id
    private Integer id;

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
     * 获取假期类型id
     *
     * @return vacation_id - 假期类型id
     */
    public Integer getVacationId() {
        return vacationId;
    }

    /**
     * 设置假期类型id
     *
     * @param vacationId 假期类型id
     */
    public void setVacationId(Integer vacationId) {
        this.vacationId = vacationId;
    }

    /**
     * 获取开始时间
     *
     * @return start_time - 开始时间
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * 设置开始时间
     *
     * @param startTime 开始时间
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * 获取结束时间
     *
     * @return end_time - 结束时间
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * 设置结束时间
     *
     * @param endTime 结束时间
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * 获取请假原因
     *
     * @return leave_reason - 请假原因
     */
    public String getLeaveReason() {
        return leaveReason;
    }

    /**
     * 设置请假原因
     *
     * @param leaveReason 请假原因
     */
    public void setLeaveReason(String leaveReason) {
        this.leaveReason = leaveReason;
    }

    /**
     * 获取图片（用逗号隔开）
     *
     * @return leave_picture - 图片（用逗号隔开）
     */
    public String getLeavePicture() {
        return leavePicture;
    }

    /**
     * 设置图片（用逗号隔开）
     *
     * @param leavePicture 图片（用逗号隔开）
     */
    public void setLeavePicture(String leavePicture) {
        this.leavePicture = leavePicture;
    }

    /**
     * 获取审批条件表id
     *
     * @return approval_criteria_id - 审批条件表id
     */
    public Integer getApprovalCriteriaId() {
        return approvalCriteriaId;
    }

    /**
     * 设置审批条件表id
     *
     * @param approvalCriteriaId 审批条件表id
     */
    public void setApprovalCriteriaId(Integer approvalCriteriaId) {
        this.approvalCriteriaId = approvalCriteriaId;
    }

    /**
     * 获取审批状态 0 审批中 1通过 2拒绝 3撤回
     *
     * @return apprpval_status - 审批状态 0 审批中 1通过 2拒绝 3撤回
     */
    public String getApprpvalStatus() {
        return apprpvalStatus;
    }

    /**
     * 设置审批状态 0 审批中 1通过 2拒绝 3撤回
     *
     * @param apprpvalStatus 审批状态 0 审批中 1通过 2拒绝 3撤回
     */
    public void setApprpvalStatus(String apprpvalStatus) {
        this.apprpvalStatus = apprpvalStatus;
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