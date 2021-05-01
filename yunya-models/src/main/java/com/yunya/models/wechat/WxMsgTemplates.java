package com.yunya.models.wechat;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

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
     * 类别
     */
    private Integer category;

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

    @Column(name = "is_deleted")
    private Boolean isDeleted;

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
     * 获取模板标题
     *
     * @return title - 模板标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置模板标题
     *
     * @param title 模板标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取模板内容
     *
     * @return content - 模板内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置模板内容
     *
     * @param content 模板内容
     */
    public void setContent(String content) {
        this.content = content;
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
     * 获取使用场景
     *
     * @return scene - 使用场景
     */
    public String getScene() {
        return scene;
    }

    /**
     * 设置使用场景
     *
     * @param scene 使用场景
     */
    public void setScene(String scene) {
        this.scene = scene;
    }

    /**
     * 获取是否启用
     *
     * @return is_enabled - 是否启用
     */
    public Boolean getIsEnabled() {
        return isEnabled;
    }

    /**
     * 设置是否启用
     *
     * @param isEnabled 是否启用
     */
    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
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
     * @return is_deleted
     */
    public Boolean getIsDeleted() {
        return isDeleted;
    }

    /**
     * @param isDeleted
     */
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}