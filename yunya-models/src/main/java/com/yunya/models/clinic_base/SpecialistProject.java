package com.yunya.models.clinic_base;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "specialist_project")
public class SpecialistProject {
    /**
     * 专科项目ID
     */
    @Id
    private Integer id;

    /**
     * 专科项目名称
     */
    private String name;

    /**
     * 价目项目ID列表
     */
    @Column(name = "tariff_ids")
    private String tariffIds;

    /**
     * 商品项目ID列表
     */
    @Column(name = "oral_ids")
    private String oralIds;

    /**
     * 是否有效/是否启用
     */
    private Boolean inservice;

    /**
     * 创建人ID
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建人姓名
     */
    @Column(name = "crt_name")
    private String crtName;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人ID
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 更新人姓名
     */
    @Column(name = "upd_name")
    private String updName;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 获取专科项目ID
     *
     * @return id - 专科项目ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置专科项目ID
     *
     * @param id 专科项目ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取专科项目名称
     *
     * @return name - 专科项目名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置专科项目名称
     *
     * @param name 专科项目名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取项目ID列表
     *
     * @return tariff_ids - 项目ID列表
     */
    public String getTariffIds() {
        return tariffIds;
    }

    /**
     * 设置项目ID列表
     *
     * @param tariffIds 项目ID列表
     */
    public void setTariffIds(String tariffIds) {
        this.tariffIds = tariffIds;
    }

    public void setOralIds(String oralIds) {
        this.oralIds = oralIds;
    }

    public String getOralIds() {
        return oralIds;
    }

    /**
     * 获取是否有效/是否启用
     *
     * @return inservice - 是否有效/是否启用
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否有效/是否启用
     *
     * @param inservice 是否有效/是否启用
     */
    public void setInservice(Boolean inservice) {
        this.inservice = inservice;
    }

    /**
     * 获取创建人ID
     *
     * @return crt_id - 创建人ID
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置创建人ID
     *
     * @param crtId 创建人ID
     */
    public void setCrtId(Integer crtId) {
        this.crtId = crtId;
    }

    /**
     * 获取创建人姓名
     *
     * @return crt_name - 创建人姓名
     */
    public String getCrtName() {
        return crtName;
    }

    /**
     * 设置创建人姓名
     *
     * @param crtName 创建人姓名
     */
    public void setCrtName(String crtName) {
        this.crtName = crtName;
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
     * 获取更新人ID
     *
     * @return upd_id - 更新人ID
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * 设置更新人ID
     *
     * @param updId 更新人ID
     */
    public void setUpdId(Integer updId) {
        this.updId = updId;
    }

    /**
     * 获取更新人姓名
     *
     * @return upd_name - 更新人姓名
     */
    public String getUpdName() {
        return updName;
    }

    /**
     * 设置更新人姓名
     *
     * @param updName 更新人姓名
     */
    public void setUpdName(String updName) {
        this.updName = updName;
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
}