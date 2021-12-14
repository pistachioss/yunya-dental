package com.yunya.models.report;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "stat_emp_treat")
public class StatEmpTreat {
    /**
     * 门诊id
     */
    @Id
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 医生id
     */
    @Id
    @Column(name = "dentist_id")
    private Integer dentistId;

    /**
     * 就诊日期
     */
    @Id
    @Column(name = "treat_date")
    private Integer treatDate;

    /**
     * 初诊人数
     */
    @Column(name = "first_visit_count")
    private Integer firstVisitCount;

    /**
     * 复诊人数
     */
    @Column(name = "re_visit_count")
    private Integer reVisitCount;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 创建人id
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 获取门诊id
     *
     * @return org_id - 门诊id
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置门诊id
     *
     * @param orgId 门诊id
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取医生id
     *
     * @return dentist_id - 医生id
     */
    public Integer getDentistId() {
        return dentistId;
    }

    /**
     * 设置医生id
     *
     * @param dentistId 医生id
     */
    public void setDentistId(Integer dentistId) {
        this.dentistId = dentistId;
    }

    /**
     * 获取就诊日期
     *
     * @return treat_date - 就诊日期
     */
    public Integer getTreatDate() {
        return treatDate;
    }

    /**
     * 设置就诊日期
     *
     * @param treatDate 就诊日期
     */
    public void setTreatDate(Integer treatDate) {
        this.treatDate = treatDate;
    }

    /**
     * 获取初诊人数
     *
     * @return first_visit_count - 初诊人数
     */
    public Integer getFirstVisitCount() {
        return firstVisitCount;
    }

    /**
     * 设置初诊人数
     *
     * @param firstVisitCount 初诊人数
     */
    public void setFirstVisitCount(Integer firstVisitCount) {
        this.firstVisitCount = firstVisitCount;
    }

    /**
     * 获取复诊人数
     *
     * @return re_visit_count - 复诊人数
     */
    public Integer getReVisitCount() {
        return reVisitCount;
    }

    /**
     * 设置复诊人数
     *
     * @param reVisitCount 复诊人数
     */
    public void setReVisitCount(Integer reVisitCount) {
        this.reVisitCount = reVisitCount;
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
     * 获取创建人id
     *
     * @return crt_id - 创建人id
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置创建人id
     *
     * @param crtId 创建人id
     */
    public void setCrtId(Integer crtId) {
        this.crtId = crtId;
    }
}