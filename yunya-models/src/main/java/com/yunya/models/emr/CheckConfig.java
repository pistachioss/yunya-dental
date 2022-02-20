package com.yunya.models.emr;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "check_config")
public class CheckConfig {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 检查名称
     */
    @Column(name = "check_name")
    private String checkName;

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
     * 更新人id
     */
    @Column(name = "upt_id")
    private Integer uptId;

    /**
     * 更新时间
     */
    @Column(name = "upt_time")
    private Date uptTime;

    /**
     * 是否启用
     */
    private Boolean inservice;

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
     * 获取检查名称
     *
     * @return check_name - 检查名称
     */
    public String getCheckName() {
        return checkName;
    }

    /**
     * 设置检查名称
     *
     * @param checkName 检查名称
     */
    public void setCheckName(String checkName) {
        this.checkName = checkName;
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
     * 获取更新人id
     *
     * @return upt_id - 更新人id
     */
    public Integer getUptId() {
        return uptId;
    }

    /**
     * 设置更新人id
     *
     * @param uptId 更新人id
     */
    public void setUptId(Integer uptId) {
        this.uptId = uptId;
    }

    /**
     * 获取更新时间
     *
     * @return upt_time - 更新时间
     */
    public Date getUptTime() {
        return uptTime;
    }

    /**
     * 设置更新时间
     *
     * @param uptTime 更新时间
     */
    public void setUptTime(Date uptTime) {
        this.uptTime = uptTime;
    }

    /**
     * 获取是否启用
     *
     * @return inservice - 是否启用
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否启用
     *
     * @param inservice 是否启用
     */
    public void setInservice(Boolean inservice) {
        this.inservice = inservice;
    }
}