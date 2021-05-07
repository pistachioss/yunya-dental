package com.yunya.models.wechat;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "wx_template_msg_records")
@Data
public class WxTemplateMsgRecords {
    /**
     * 消息ID
     */
    private Long id;

    /**
     * 消息ID
     */
    @Column(name = "msg_id")
    private String msgId;

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
}