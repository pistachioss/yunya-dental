package com.yunya.models.emr;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "treat_plan_step")
public class TreatPlanStep {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 治疗计划id
     */
    @Column(name = "plan_id")
    private Integer planId;

    /**
     * 步骤名称
     */
    @Column(name = "step_name")
    private String stepName;

    /**
     * 创建人id
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人id
     */
    @Column(name = "upt_id")
    private Integer uptId;

    /**
     * 更新时间
     */
    @Column(name = "upt_time")
    private Date uptTime;

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
     * 获取治疗计划id
     *
     * @return plan_id - 治疗计划id
     */
    public Integer getPlanId() {
        return planId;
    }

    /**
     * 设置治疗计划id
     *
     * @param planId 治疗计划id
     */
    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    /**
     * 获取步骤名称
     *
     * @return step_name - 步骤名称
     */
    public String getStepName() {
        return stepName;
    }

    /**
     * 设置步骤名称
     *
     * @param stepName 步骤名称
     */
    public void setStepName(String stepName) {
        this.stepName = stepName;
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
     * 获取更新人id
     *
     * @return upt_id - 更新人id
     */
    public Integer getUptId() {
        return uptId;
    }

    /**
     * 设置更新人id
     *
     * @param uptId 更新人id
     */
    public void setUptId(Integer uptId) {
        this.uptId = uptId;
    }

    /**
     * 获取更新时间
     *
     * @return upt_time - 更新时间
     */
    public Date getUptTime() {
        return uptTime;
    }

    /**
     * 设置更新时间
     *
     * @param uptTime 更新时间
     */
    public void setUptTime(Date uptTime) {
        this.uptTime = uptTime;
    }
}