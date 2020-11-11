package com.yunya.models.employee_attend;

import java.util.Date;
import javax.persistence.*;

@Table(name = "approval_level_set")
public class ApprovalLevelSet {
    @Id
    private Integer id;

    /**
     * 审批条件表id
     */
    @Column(name = "approval_criteria_id")
    private Integer approvalCriteriaId;

    /**
     * 审批级别名称
     */
    @Column(name = "approval_level_name")
    private String approvalLevelName;

    /**
     * 审批优先级 数字越小级别越高
     */
    @Column(name = "approval_priority")
    private Integer approvalPriority;

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
     * 获取审批级别名称
     *
     * @return approval_level_name - 审批级别名称
     */
    public String getApprovalLevelName() {
        return approvalLevelName;
    }

    /**
     * 设置审批级别名称
     *
     * @param approvalLevelName 审批级别名称
     */
    public void setApprovalLevelName(String approvalLevelName) {
        this.approvalLevelName = approvalLevelName;
    }

    /**
     * 获取审批优先级 数字越小级别越高
     *
     * @return approval_priority - 审批优先级 数字越小级别越高
     */
    public Integer getApprovalPriority() {
        return approvalPriority;
    }

    /**
     * 设置审批优先级 数字越小级别越高
     *
     * @param approvalPriority 审批优先级 数字越小级别越高
     */
    public void setApprovalPriority(Integer approvalPriority) {
        this.approvalPriority = approvalPriority;
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