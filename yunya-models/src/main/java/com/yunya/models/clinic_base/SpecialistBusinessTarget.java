package com.yunya.models.clinic_base;

import java.util.Date;
import javax.persistence.*;

@Table(name = "specialist_business_target")
public class SpecialistBusinessTarget {
    /**
     * 专科数量目标ID
     */
    @Id
    private Integer id;

    /**
     * 专科项目ID
     */
    @Column(name = "specialist_project_id")
    private Integer specialistProjectId;

    /**
     * 组织ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 目标数量
     */
    @Column(name = "business_goal")
    private Integer businessGoal;

    /**
     * 完成目标时间
     */
    @Column(name = "business_date")
    private String businessDate;

    /**
     * 是否有效/是否启用
     */
    private Boolean inservice;

    /**
     * 创建人ID
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建人姓名
     */
    @Column(name = "crt_name")
    private String crtName;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人ID
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 更新人姓名
     */
    @Column(name = "upd_name")
    private String updName;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 获取专科数量目标ID
     *
     * @return id - 专科数量目标ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置专科数量目标ID
     *
     * @param id 专科数量目标ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取专科项目ID
     *
     * @return specialist_project_id - 专科项目ID
     */
    public Integer getSpecialistProjectId() {
        return specialistProjectId;
    }

    /**
     * 设置专科项目ID
     *
     * @param specialistProjectId 专科项目ID
     */
    public void setSpecialistProjectId(Integer specialistProjectId) {
        this.specialistProjectId = specialistProjectId;
    }

    /**
     * 获取组织ID
     *
     * @return org_id - 组织ID
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置组织ID
     *
     * @param orgId 组织ID
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取目标数量
     *
     * @return business_goal - 目标数量
     */
    public Integer getBusinessGoal() {
        return businessGoal;
    }

    /**
     * 设置目标数量
     *
     * @param businessGoal 目标数量
     */
    public void setBusinessGoal(Integer businessGoal) {
        this.businessGoal = businessGoal;
    }

    /**
     * 获取完成目标时间
     *
     * @return business_date - 完成目标时间
     */
    public String getBusinessDate() {
        return businessDate;
    }

    /**
     * 设置完成目标时间
     *
     * @param businessDate 完成目标时间
     */
    public void setBusinessDate(String businessDate) {
        this.businessDate = businessDate;
    }

    /**
     * 获取是否有效/是否启用
     *
     * @return inservice - 是否有效/是否启用
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否有效/是否启用
     *
     * @param inservice 是否有效/是否启用
     */
    public void setInservice(Boolean inservice) {
        this.inservice = inservice;
    }

    /**
     * 获取创建人ID
     *
     * @return crt_id - 创建人ID
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置创建人ID
     *
     * @param crtId 创建人ID
     */
    public void setCrtId(Integer crtId) {
        this.crtId = crtId;
    }

    /**
     * 获取创建人姓名
     *
     * @return crt_name - 创建人姓名
     */
    public String getCrtName() {
        return crtName;
    }

    /**
     * 设置创建人姓名
     *
     * @param crtName 创建人姓名
     */
    public void setCrtName(String crtName) {
        this.crtName = crtName;
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
     * 获取更新人ID
     *
     * @return upd_id - 更新人ID
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * 设置更新人ID
     *
     * @param updId 更新人ID
     */
    public void setUpdId(Integer updId) {
        this.updId = updId;
    }

    /**
     * 获取更新人姓名
     *
     * @return upd_name - 更新人姓名
     */
    public String getUpdName() {
        return updName;
    }

    /**
     * 设置更新人姓名
     *
     * @param updName 更新人姓名
     */
    public void setUpdName(String updName) {
        this.updName = updName;
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