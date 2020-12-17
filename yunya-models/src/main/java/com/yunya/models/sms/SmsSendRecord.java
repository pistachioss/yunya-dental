package com.yunya.models.sms;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "sms_send_record")
public class SmsSendRecord {
    @Id
    private Integer id;

    /**
     * 组织id（门诊、公司）
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 短信发送批次id
     */
    @Column(name = "batch_id")
    private Integer batchId;

    /**
     * 接收者id
     */
    @Column(name = "receiver_id")
    private Integer receiverId;

    /**
     * 发送对象
     */
    @Column(name = "send_object")
    private String sendObject;

    /**
     * 手机号（接收者）
     */
    private String mobile;

    /**
     * 短信条数（阿里短信内容的所占条数）
     */
    @Column(name = "content_num")
    private Integer contentNum;

    /**
     * 发送状态：0-发送中，1-发送成功，2-发送失败
     */
    private Byte status;

    /**
     * 发送回执ID
     */
    @Column(name = "biz_id")
    private String bizId;

    /**
     * 回执错误描述
     */
    @Column(name = "biz_msg")
    private String bizMsg;

    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 发送人
     */
    @Column(name = "crt_user")
    private String crtUser;

    @Column(name = "crt_time")
    private Date crtTime;

    @Column(name = "upt_id")
    private Integer uptId;

    @Column(name = "upt_time")
    private Date uptTime;

    /**
     * 短信内容
     */
    private String content;

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
     * 获取短信发送批次id
     *
     * @return batch_id - 短信发送批次id
     */
    public Integer getBatchId() {
        return batchId;
    }

    /**
     * 设置短信发送批次id
     *
     * @param batchId 短信发送批次id
     */
    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    /**
     * 获取接收者id
     *
     * @return receiver_id - 接收者id
     */
    public Integer getReceiverId() {
        return receiverId;
    }

    /**
     * 设置接收者id
     *
     * @param receiverId 接收者id
     */
    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    /**
     * 设置发送对象
     *
     * @param sendObject 发送对象
     */
    public void setSendObject(String sendObject) {
        this.sendObject = sendObject;
    }

    /**
     * 获取发送对象
     *
     * @return
     */
    public String getSendObject() {
        return sendObject;
    }

    /**
     * 获取手机号（接收者）
     *
     * @return mobile - 手机号（接收者）
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * 设置手机号（接收者）
     *
     * @param mobile 手机号（接收者）
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * 获取短信条数（阿里短信内容的所占条数）
     *
     * @return content_num - 短信条数（阿里短信内容的所占条数）
     */
    public Integer getContentNum() {
        return contentNum;
    }

    /**
     * 设置短信条数（阿里短信内容的所占条数）
     *
     * @param contentNum 短信条数（阿里短信内容的所占条数）
     */
    public void setContentNum(Integer contentNum) {
        this.contentNum = contentNum;
    }

    /**
     * 获取发送状态：0-发送中，1-发送成功，2-发送失败
     *
     * @return status - 发送状态：0-发送中，1-发送成功，2-发送失败
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置发送状态：0-发送中，1-发送成功，2-发送失败
     *
     * @param status 发送状态：0-发送中，1-发送成功，2-发送失败
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * 获取发送回执ID
     *
     * @return biz_id - 发送回执ID
     */
    public String getBizId() {
        return bizId;
    }

    /**
     * 设置发送回执ID
     *
     * @param bizId 发送回执ID
     */
    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    /**
     * 获取回执错误描述
     *
     * @return biz_msg - 回执错误描述
     */
    public String getBizMsg() {
        return bizMsg;
    }

    /**
     * 设置回执错误描述
     *
     * @param bizMsg 回执错误描述
     */
    public void setBizMsg(String bizMsg) {
        this.bizMsg = bizMsg;
    }

    /**
     * @return crt_id
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * @param crtId
     */
    public void setCrtId(Integer crtId) {
        this.crtId = crtId;
    }

    /**
     * 获取发送人
     *
     * @return crt_user - 发送人
     */
    public String getCrtUser() {
        return crtUser;
    }

    /**
     * 设置发送人
     *
     * @param crtUser 发送人
     */
    public void setCrtUser(String crtUser) {
        this.crtUser = crtUser;
    }

    /**
     * @return crt_time
     */
    public Date getCrtTime() {
        return crtTime;
    }

    /**
     * @param crtTime
     */
    public void setCrtTime(Date crtTime) {
        this.crtTime = crtTime;
    }

    /**
     * @return upt_id
     */
    public Integer getUptId() {
        return uptId;
    }

    /**
     * @param uptId
     */
    public void setUptId(Integer uptId) {
        this.uptId = uptId;
    }

    /**
     * @return upt_time
     */
    public Date getUptTime() {
        return uptTime;
    }

    /**
     * @param uptTime
     */
    public void setUptTime(Date uptTime) {
        this.uptTime = uptTime;
    }

    /**
     * 获取短信内容
     *
     * @return content - 短信内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置短信内容
     *
     * @param content 短信内容
     */
    public void setContent(String content) {
        this.content = content;
    }
}