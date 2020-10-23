package com.yunya.models.middletable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "base_visit_remind")
public class BaseVisitRemind {
    /**
     * 随访/提醒ID
     */
    @Id
    @Column(name = "record_id")
    private Integer recordId;

    /**
     * 组织ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 患者ID
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 数据类型（0-随访；1-提醒）
     */
    @Id
    private Byte type;

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 随访/提醒时间
     */
    private Date time;

    /**
     * 随访/提醒内容
     */
    private String content;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 获取随访/提醒ID
     *
     * @return record_id - 随访/提醒ID
     */
    public Integer getRecordId() {
        return recordId;
    }

    /**
     * 设置随访/提醒ID
     *
     * @param recordId 随访/提醒ID
     */
    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
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
     * 获取患者ID
     *
     * @return patient_id - 患者ID
     */
    public Integer getPatientId() {
        return patientId;
    }

    /**
     * 设置患者ID
     *
     * @param patientId 患者ID
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * 获取数据类型（0-随访；1-提醒）
     *
     * @return type - 数据类型（0-随访；1-提醒）
     */
    public Byte getType() {
        return type;
    }

    /**
     * 设置数据类型（0-随访；1-提醒）
     *
     * @param type 数据类型（0-随访；1-提醒）
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * 获取用户ID
     *
     * @return user_id - 用户ID
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     *
     * @param userId 用户ID
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取随访/提醒时间
     *
     * @return time - 随访/提醒时间
     */
    public Date getTime() {
        return time;
    }

    /**
     * 设置随访/提醒时间
     *
     * @param time 随访/提醒时间
     */
    public void setTime(Date time) {
        this.time = time;
    }

    /**
     * 获取随访/提醒内容
     *
     * @return content - 随访/提醒内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置随访/提醒内容
     *
     * @param content 随访/提醒内容
     */
    public void setContent(String content) {
        this.content = content;
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