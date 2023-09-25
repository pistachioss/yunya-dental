package com.yunya.models.treatment_other;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tariff_package")
public class TariffPackage {
    /**
     * 组合id
     */
    @Id
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 是否启用
     */
    private Boolean inservice;

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
     * 获取组合id
     *
     * @return id - 组合id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置组合id
     *
     * @param id 组合id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取名称
     *
     * @return name - 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
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
}