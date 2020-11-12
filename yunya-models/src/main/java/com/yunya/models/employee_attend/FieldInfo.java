package com.yunya.models.employee_attend;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import javax.persistence.*;

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
     * 获取外勤地址
     *
     * @return field_address - 外勤地址
     */
    public String getFieldAddress() {
        return fieldAddress;
    }

    /**
     * 设置外勤地址
     *
     * @param fieldAddress 外勤地址
     */
    public void setFieldAddress(String fieldAddress) {
        this.fieldAddress = fieldAddress;
    }

    /**
     * @return start_time
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @param startTime
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @return end_time
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * 获取外勤原因
     *
     * @return field_reason - 外勤原因
     */
    public String getFieldReason() {
        return fieldReason;
    }

    /**
     * 设置外勤原因
     *
     * @param fieldReason 外勤原因
     */
    public void setFieldReason(String fieldReason) {
        this.fieldReason = fieldReason;
    }

    /**
     * 获取审批人id （直接存员工id 与审批人员表无关）
     *
     * @return approval_people_id - 审批人id （直接存员工id 与审批人员表无关）
     */
    public Integer getApprovalPeopleId() {
        return approvalPeopleId;
    }

    /**
     * 设置审批人id （直接存员工id 与审批人员表无关）
     *
     * @param approvalPeopleId 审批人id （直接存员工id 与审批人员表无关）
     */
    public void setApprovalPeopleId(Integer approvalPeopleId) {
        this.approvalPeopleId = approvalPeopleId;
    }

    /**
     * 获取审批状态 0 审批中 1通过 2拒绝  3撤回 4过期
     *
     * @return apprpval_status - 审批状态 0 审批中 1通过 2拒绝  3撤回 4过期
     */
    public String getApprpvalStatus() {
        return apprpvalStatus;
    }

    /**
     * 设置审批状态 0 审批中 1通过 2拒绝  3撤回 4过期
     *
     * @param apprpvalStatus 审批状态 0 审批中 1通过 2拒绝  3撤回 4过期
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