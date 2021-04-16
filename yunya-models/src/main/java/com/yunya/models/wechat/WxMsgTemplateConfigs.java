package com.yunya.models.wechat;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "wx_msg_template_configs")
public class WxMsgTemplateConfigs {
    @Id
    private Integer id;

    /**
     * 模板ID
     */
    @Column(name = "template_id")
    private String templateId;

    /**
     * 类别
     */
    private Integer category;

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
}