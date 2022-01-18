package com.yunya.models.emr;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "treat_plan_step_history")
public class TreatPlanStepHistory {
    @Id
    private Integer id;

    /**
     * 治疗计划步骤id
     */
    @Column(name = "step_id")
    private Integer stepId;

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
     * 写操作类型：0-新增，1-修改，2-删除
     */
    private Byte operation;

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
     * 获取治疗计划步骤id
     *
     * @return step_id - 治疗计划步骤id
     */
    public Integer getStepId() {
        return stepId;
    }

    /**
     * 设置治疗计划步骤id
     *
     * @param stepId 治疗计划步骤id
     */
    public void setStepId(Integer stepId) {
        this.stepId = stepId;
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
     * 获取写操作类型：0-新增，1-修改，2-删除
     *
     * @return operation - 写操作类型：0-新增，1-修改，2-删除
     */
    public Byte getOperation() {
        return operation;
    }

    /**
     * 设置写操作类型：0-新增，1-修改，2-删除
     *
     * @param operation 写操作类型：0-新增，1-修改，2-删除
     */
    public void setOperation(Byte operation) {
        this.operation = operation;
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
}