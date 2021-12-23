package com.yunya.report.ultimate.model;

import java.util.Date;
import javax.persistence.*;

@Table(name = "base_peizhen_centre")
public class BasePeizhenCentre {
    /**
     * 诊疗记录ID
     */
    @Column(name = "treatment_id")
    private Integer treatmentId;

    /**
     * 组织ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 接诊开始时间
     */
    @Column(name = "treat_start_time")
    private Date treatStartTime;

    /**
     * 接诊结束时间
     */
    @Column(name = "treat_end_time")
    private Date treatEndTime;

    /**
     * 助手1
     */
    @Column(name = "assistant_1")
    private String assistant1;

    /**
     * 助手2
     */
    @Column(name = "assistant_2")
    private String assistant2;

    /**
     * 助手3
     */
    @Column(name = "assistant_3")
    private String assistant3;

    /**
     * 应收工作量
     */
    @Column(name = "actualWorkload")
    private String actualworkload;

    /**
     * 退费金额
     */
    @Column(name = "refundWorkload")
    private String refundworkload;

    /**
     * 获取诊疗记录ID
     *
     * @return treatment_id - 诊疗记录ID
     */
    public Integer getTreatmentId() {
        return treatmentId;
    }

    /**
     * 设置诊疗记录ID
     *
     * @param treatmentId 诊疗记录ID
     */
    public void setTreatmentId(Integer treatmentId) {
        this.treatmentId = treatmentId;
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
     * 获取接诊开始时间
     *
     * @return treat_start_time - 接诊开始时间
     */
    public Date getTreatStartTime() {
        return treatStartTime;
    }

    /**
     * 设置接诊开始时间
     *
     * @param treatStartTime 接诊开始时间
     */
    public void setTreatStartTime(Date treatStartTime) {
        this.treatStartTime = treatStartTime;
    }

    /**
     * 获取接诊结束时间
     *
     * @return treat_end_time - 接诊结束时间
     */
    public Date getTreatEndTime() {
        return treatEndTime;
    }

    /**
     * 设置接诊结束时间
     *
     * @param treatEndTime 接诊结束时间
     */
    public void setTreatEndTime(Date treatEndTime) {
        this.treatEndTime = treatEndTime;
    }

    /**
     * 获取助手1
     *
     * @return assistant_1 - 助手1
     */
    public String getAssistant1() {
        return assistant1;
    }

    /**
     * 设置助手1
     *
     * @param assistant1 助手1
     */
    public void setAssistant1(String assistant1) {
        this.assistant1 = assistant1;
    }

    /**
     * 获取助手2
     *
     * @return assistant_2 - 助手2
     */
    public String getAssistant2() {
        return assistant2;
    }

    /**
     * 设置助手2
     *
     * @param assistant2 助手2
     */
    public void setAssistant2(String assistant2) {
        this.assistant2 = assistant2;
    }

    /**
     * 获取助手3
     *
     * @return assistant_3 - 助手3
     */
    public String getAssistant3() {
        return assistant3;
    }

    /**
     * 设置助手3
     *
     * @param assistant3 助手3
     */
    public void setAssistant3(String assistant3) {
        this.assistant3 = assistant3;
    }

    /**
     * 获取应收工作量
     *
     * @return actualWorkload - 应收工作量
     */
    public String getActualworkload() {
        return actualworkload;
    }

    /**
     * 设置应收工作量
     *
     * @param actualworkload 应收工作量
     */
    public void setActualworkload(String actualworkload) {
        this.actualworkload = actualworkload;
    }

    /**
     * 获取退费金额
     *
     * @return refundWorkload - 退费金额
     */
    public String getRefundworkload() {
        return refundworkload;
    }

    /**
     * 设置退费金额
     *
     * @param refundworkload 退费金额
     */
    public void setRefundworkload(String refundworkload) {
        this.refundworkload = refundworkload;
    }
}