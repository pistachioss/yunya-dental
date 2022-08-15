package com.yunya365.mini.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "self_mention_clinic")
public class SelfMentionClinic {
    @Id
    private Integer id;

    /**
     * 门诊id
     */
    @Column(name = "clinic_id")
    private Integer clinicId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

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
     * 获取门诊id
     *
     * @return clinic_id - 门诊id
     */
    public Integer getClinicId() {
        return clinicId;
    }

    /**
     * 设置门诊id
     *
     * @param clinicId 门诊id
     */
    public void setClinicId(Integer clinicId) {
        this.clinicId = clinicId;
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