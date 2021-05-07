package com.yunya.models.wechat;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "wx_template_msg_records")
public class WxTemplateMsgRecords {
    /**
     * 消息ID
     */
    private Long id;

    /**
     * 模板ID
     */
    @Column(name = "template_id")
    private String templateId;

    /**
     * 患者id
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 患者姓名
     */
    @Column(name = "patient_name")
    private String patientName;

    /**
     * 粉丝ID
     */
    @Column(name = "open_id")
    private String openId;

    /**
     * 粉丝昵称
     */
    @Column(name = "nick_name")
    private String nickName;

    /**
     * 类别
     */
    private Integer category;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息状态
     */
    @Column(name = "msg_status")
    private Integer msgStatus;

    /**
     * 消息日期
     */
    @Column(name = "msg_date")
    private Date msgDate;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 获取消息ID
     *
     * @return id - 消息ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置消息ID
     *
     * @param id 消息ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取模板ID
     *
     * @return template_id - 模板ID
     */
    public String getTemplateId() {
        return templateId;
    }

    /**
     * 设置模板ID
     *
     * @param templateId 模板ID
     */
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    /**
     * 获取患者id
     *
     * @return patient_id - 患者id
     */
    public Integer getPatientId() {
        return patientId;
    }

    /**
     * 设置患者id
     *
     * @param patientId 患者id
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * 获取患者姓名
     *
     * @return patient_name - 患者姓名
     */
    public String getPatientName() {
        return patientName;
    }

    /**
     * 设置患者姓名
     *
     * @param patientName 患者姓名
     */
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    /**
     * 获取粉丝ID
     *
     * @return open_id - 粉丝ID
     */
    public String getOpenId() {
        return openId;
    }

    /**
     * 设置粉丝ID
     *
     * @param openId 粉丝ID
     */
    public void setOpenId(String openId) {
        this.openId = openId;
    }

    /**
     * 获取粉丝昵称
     *
     * @return nick_name - 粉丝昵称
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * 设置粉丝昵称
     *
     * @param nickName 粉丝昵称
     */
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    /**
     * 获取类别
     *
     * @return category - 类别
     */
    public Integer getCategory() {
        return category;
    }

    /**
     * 设置类别
     *
     * @param category 类别
     */
    public void setCategory(Integer category) {
        this.category = category;
    }

    /**
     * 获取消息内容
     *
     * @return content - 消息内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置消息内容
     *
     * @param content 消息内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取消息状态
     *
     * @return msg_status - 消息状态
     */
    public Integer getMsgStatus() {
        return msgStatus;
    }

    /**
     * 设置消息状态
     *
     * @param msgStatus 消息状态
     */
    public void setMsgStatus(Integer msgStatus) {
        this.msgStatus = msgStatus;
    }

    /**
     * 获取消息日期
     *
     * @return msg_date - 消息日期
     */
    public Date getMsgDate() {
        return msgDate;
    }

    /**
     * 设置消息日期
     *
     * @param msgDate 消息日期
     */
    public void setMsgDate(Date msgDate) {
        this.msgDate = msgDate;
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