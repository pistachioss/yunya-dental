package com.yunya.models.treatment_other;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "chat_message_record")
public class ChatMessageRecord {
    /**
     * 消息id
     */
    @Id
    private Integer id;

    /**
     * 发送者id
     */
    @Column(name = "send_id")
    private Integer sendId;

    /**
     * 接收者id
     */
    @Column(name = "receive_id")
    private Integer receiveId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 发送模型：0-一对一，1-群发
     */
    private Byte mode;

    /**
     * 是否已读
     */
    @Column(name = "had_read")
    private Boolean hadRead;

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
     * 获取消息id
     *
     * @return id - 消息id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置消息id
     *
     * @param id 消息id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取发送者id
     *
     * @return send_id - 发送者id
     */
    public Integer getSendId() {
        return sendId;
    }

    /**
     * 设置发送者id
     *
     * @param sendId 发送者id
     */
    public void setSendId(Integer sendId) {
        this.sendId = sendId;
    }

    /**
     * 获取接收者id
     *
     * @return receive_id - 接收者id
     */
    public Integer getReceiveId() {
        return receiveId;
    }

    /**
     * 设置接收者id
     *
     * @param receiveId 接收者id
     */
    public void setReceiveId(Integer receiveId) {
        this.receiveId = receiveId;
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
     * 获取发送模型：0-一对一，1-群发
     *
     * @return mode - 发送模型：0-一对一，1-群发
     */
    public Byte getMode() {
        return mode;
    }

    /**
     * 设置发送模型：0-一对一，1-群发
     *
     * @param mode 发送模型：0-一对一，1-群发
     */
    public void setMode(Byte mode) {
        this.mode = mode;
    }

    /**
     * 获取是否已读
     *
     * @return had_read - 是否已读
     */
    public Boolean getHadRead() {
        return hadRead;
    }

    /**
     * 设置是否已读
     *
     * @param hadRead 是否已读
     */
    public void setHadRead(Boolean hadRead) {
        this.hadRead = hadRead;
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