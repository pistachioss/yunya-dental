package com.yunya.models.sms;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "sms_send_batch")
public class SmsSendBatch {
    private Integer id;

    /**
     * 组织id（门诊、公司）
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 发送短信的条数（接收者人数）
     */
    @Column(name = "send_num")
    private Integer sendNum;

    /**
     * 回执id
     */
    @Column(name = "biz_id")
    private String bizId;

    /**
     * 回执错误信息
     */
    @Column(name = "biz_msg")
    private String bizMsg;

    /**
     * 短信类型 0：验证码。1：短信通知。2：推广短信。3：国际/港澳台消息。
     */
    private Byte type;

    /**
     * 模板id
     */
    @Column(name = "template_id")
    private Integer templateId;

    /**
     * 发送操作人id
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 发送操作人姓名
     */
    @Column(name = "crt_user")
    private String crtUser;

    /**
     * 发送时间
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
     * 获取模板id
     *
     * @return
     */
    public Integer getTemplateId() {
        return templateId;
    }

    /**
     * 设置模板id
     *
     * @param templateId 模板id
     */
    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    /**
     * 获取发送短信的条数（接收者人数）
     *
     * @return send_num - 发送短信的条数（接收者人数）
     */
    public Integer getSendNum() {
        return sendNum;
    }

    /**
     * 设置发送短信的条数（接收者人数）
     *
     * @param sendNum 发送短信的条数（接收者人数）
     */
    public void setSendNum(Integer sendNum) {
        this.sendNum = sendNum;
    }

    /**
     * 获取回执id
     *
     * @return biz_id - 回执id
     */
    public String getBizId() {
        return bizId;
    }

    /**
     * 设置回执id
     *
     * @param bizId 回执id
     */
    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    /**
     * 获取回执错误信息
     *
     * @return biz_msg - 回执错误信息
     */
    public String getBizMsg() {
        return bizMsg;
    }

    /**
     * 设置回执错误信息
     *
     * @param bizMsg 回执错误信息
     */
    public void setBizMsg(String bizMsg) {
        this.bizMsg = bizMsg;
    }

    /**
     * 获取短信类型 0：验证码。1：短信通知。2：推广短信。3：国际/港澳台消息。
     *
     * @return type - 短信类型 0：验证码。1：短信通知。2：推广短信。3：国际/港澳台消息。
     */
    public Byte getType() {
        return type;
    }

    /**
     * 设置短信类型 0：验证码。1：短信通知。2：推广短信。3：国际/港澳台消息。
     *
     * @param type 短信类型 0：验证码。1：短信通知。2：推广短信。3：国际/港澳台消息。
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * 获取发送操作人id
     *
     * @return crt_id - 发送操作人id
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置发送操作人id
     *
     * @param crtId 发送操作人id
     */
    public void setCrtId(Integer crtId) {
        this.crtId = crtId;
    }

    /**
     * 获取发送操作人姓名
     *
     * @return crt_user - 发送操作人姓名
     */
    public String getCrtUser() {
        return crtUser;
    }

    /**
     * 设置发送操作人姓名
     *
     * @param crtUser 发送操作人姓名
     */
    public void setCrtUser(String crtUser) {
        this.crtUser = crtUser;
    }

    /**
     * 获取发送时间
     *
     * @return crt_time - 发送时间
     */
    public Date getCrtTime() {
        return crtTime;
    }

    /**
     * 设置发送时间
     *
     * @param crtTime 发送时间
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