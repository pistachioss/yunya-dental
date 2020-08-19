package com.yunya.models.treatment;

import java.util.Date;
import javax.persistence.*;

@Table(name = "assistant_matching_record")
public class AssistantMatchingRecord {
    /**
     * 主键
     */
    @Id
    private Integer id;

    /**
     * 组织ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 就诊记录ID
     */
    @Column(name = "treatment_record_id")
    private Integer treatmentRecordId;

    /**
     * 开单记录ID
     */
    @Column(name = "order_record_id")
    private Integer orderRecordId;

    /**
     * 助手ID
     */
    @Column(name = "assistant_id")
    private Integer assistantId;

    /**
     * 助手类型（0-助手1；1-助手2；2-巡回）
     */
    private Byte type;

    /**
     * 是否有效
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

    @Column(name = "upd_id")
    private Integer updId;

    @Column(name = "upd_name")
    private String updName;

    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Integer id) {
        this.id = id;
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
     * 获取就诊记录ID
     *
     * @return treatment_record_id - 就诊记录ID
     */
    public Integer getTreatmentRecordId() {
        return treatmentRecordId;
    }

    /**
     * 设置就诊记录ID
     *
     * @param treatmentRecordId 就诊记录ID
     */
    public void setTreatmentRecordId(Integer treatmentRecordId) {
        this.treatmentRecordId = treatmentRecordId;
    }

    /**
     * 获取开单记录ID
     *
     * @return order_record_id - 开单记录ID
     */
    public Integer getOrderRecordId() {
        return orderRecordId;
    }

    /**
     * 设置开单记录ID
     *
     * @param orderRecordId 开单记录ID
     */
    public void setOrderRecordId(Integer orderRecordId) {
        this.orderRecordId = orderRecordId;
    }

    /**
     * 获取助手ID
     *
     * @return assistant_id - 助手ID
     */
    public Integer getAssistantId() {
        return assistantId;
    }

    /**
     * 设置助手ID
     *
     * @param assistantId 助手ID
     */
    public void setAssistantId(Integer assistantId) {
        this.assistantId = assistantId;
    }

    /**
     * 获取助手类型（0-助手1；1-助手2；2-巡回）
     *
     * @return type - 助手类型（0-助手1；1-助手2；2-巡回）
     */
    public Byte getType() {
        return type;
    }

    /**
     * 设置助手类型（0-助手1；1-助手2；2-巡回）
     *
     * @param type 助手类型（0-助手1；1-助手2；2-巡回）
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * 获取是否有效
     *
     * @return inservice - 是否有效
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否有效
     *
     * @param inservice 是否有效
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
     * @return upd_id
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * @param updId
     */
    public void setUpdId(Integer updId) {
        this.updId = updId;
    }

    /**
     * @return upd_name
     */
    public String getUpdName() {
        return updName;
    }

    /**
     * @param updName
     */
    public void setUpdName(String updName) {
        this.updName = updName;
    }

    /**
     * @return upd_time
     */
    public Date getUpdTime() {
        return updTime;
    }

    /**
     * @param updTime
     */
    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }
}