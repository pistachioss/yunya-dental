package com.yunya.models.wechat;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "wx_msg_templates")
public class WxMsgTemplates {
    @Id
    private Integer id;

    /**
     * 模板ID
     */
    @Column(name = "template_id")
    private String templateId;

    /**
     * 模板标题
     */
    private String title;

    /**
     * 模板内容
     */
    private String content;

    /**
     * 使用场景
     */
    private String scene;

    /**
     * 是否启用
     */
    @Column(name = "is_enabled")
    private Boolean isEnabled;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

}