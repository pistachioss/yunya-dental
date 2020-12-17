package com.yunya.models.sms;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "sms_autosend_event")
public class SmsAutosendEvent {
    @Id
    private Integer id;

    /**
     * 组织id（门诊、公司）
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 事件编码
     */
    @Column(name = "event_code")
    private String eventCode;

    /**
     * 事件名称
     */
    @Column(name = "event_name")
    private String eventName;

    /**
     * 事件所对应的业务表的主键id
     */
    @Column(name = "biz_pid")
    private Integer bizPid;

    /**
     * 短信模板id
     */
    @Column(name = "template_id")
    private Integer templateId;

    /**
     * 状态：0-关闭，1-开启
     */
    private Byte status;

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
     * 修改人id
     */
    @Column(name = "upt_id")
    private Integer uptId;

    /**
     * 修改时间
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
     * 获取组织id（门诊、公司）
     *
     * @return org_id - 组织id（门诊、公司）
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置组织id（门诊、公司）
     *
     * @param orgId 组织id（门诊、公司）
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取事件编码
     *
     * @return event_code - 事件编码
     */
    public String getEventCode() {
        return eventCode;
    }

    /**
     * 设置事件编码
     *
     * @param eventCode 事件编码
     */
    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    /**
     * 获取事件名称
     *
     * @return event_name - 事件名称
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * 设置事件名称
     *
     * @param eventName 事件名称
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * 获取事件所对应的业务表的主键id
     *
     * @return biz_pid - 事件所对应的业务表的主键id
     */
    public Integer getBizPid() {
        return bizPid;
    }

    /**
     * 设置事件所对应的业务表的主键id
     *
     * @param bizPid 事件所对应的业务表的主键id
     */
    public void setBizPid(Integer bizPid) {
        this.bizPid = bizPid;
    }

    /**
     * 获取短信模板id
     *
     * @return template_id - 短信模板id
     */
    public Integer getTemplateId() {
        return templateId;
    }

    /**
     * 设置短信模板id
     *
     * @param templateId 短信模板id
     */
    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    /**
     * 获取状态：0-关闭，1-开启
     *
     * @return status - 状态：0-关闭，1-开启
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置状态：0-关闭，1-开启
     *
     * @param status 状态：0-关闭，1-开启
     */
    public void setStatus(Byte status) {
        this.status = status;
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
     * 获取修改人id
     *
     * @return upt_id - 修改人id
     */
    public Integer getUptId() {
        return uptId;
    }

    /**
     * 设置修改人id
     *
     * @param uptId 修改人id
     */
    public void setUptId(Integer uptId) {
        this.uptId = uptId;
    }

    /**
     * 获取修改时间
     *
     * @return upt_time - 修改时间
     */
    public Date getUptTime() {
        return uptTime;
    }

    /**
     * 设置修改时间
     *
     * @param uptTime 修改时间
     */
    public void setUptTime(Date uptTime) {
        this.uptTime = uptTime;
    }
}