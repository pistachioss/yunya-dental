package com.yunya365.mini.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "expert_type")
public class ExpertType {
    @Id
    private Integer id;

    /**
     * 分类Id
     */
    @Column(name = "type_id")
    private Integer typeId;

    /**
     * 专家id
     */
    @Column(name = "expert_id")
    private Integer expertId;

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
    @Column(name = "upd_time")
    private Date updTime;

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
     * 获取分类Id
     *
     * @return type_id - 分类Id
     */
    public Integer getTypeId() {
        return typeId;
    }

    /**
     * 设置分类Id
     *
     * @param typeId 分类Id
     */
    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    /**
     * 获取专家id
     *
     * @return expert_id - 专家id
     */
    public Integer getExpertId() {
        return expertId;
    }

    /**
     * 设置专家id
     *
     * @param expertId 专家id
     */
    public void setExpertId(Integer expertId) {
        this.expertId = expertId;
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