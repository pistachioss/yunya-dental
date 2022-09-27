package com.yunya.models.appointment;

import lombok.Data;

import java.util.Date;
import javax.persistence.*;

@Table(name = "reservation_code")
@Data
public class ReservationCode {
    @Id
    private String code;

    /**
     * 是否有效，是否删除(默认有效) 1-有效；0删除
     */
    private Boolean inservice;

    /**
     * 创建人名称
     */
    @Column(name = "crt_name")
    private String crtName;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人名称
     */
    @Column(name = "upd_name")
    private String updName;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * @return code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取是否有效，是否删除(默认有效) 1-有效；0删除
     *
     * @return inservice - 是否有效，是否删除(默认有效) 1-有效；0删除
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否有效，是否删除(默认有效) 1-有效；0删除
     *
     * @param inservice 是否有效，是否删除(默认有效) 1-有效；0删除
     */
    public void setInservice(Boolean inservice) {
        this.inservice = inservice;
    }

    /**
     * 获取创建人名称
     *
     * @return crt_name - 创建人名称
     */
    public String getCrtName() {
        return crtName;
    }

    /**
     * 设置创建人名称
     *
     * @param crtName 创建人名称
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
     * 获取更新人名称
     *
     * @return upd_name - 更新人名称
     */
    public String getUpdName() {
        return updName;
    }

    /**
     * 设置更新人名称
     *
     * @param updName 更新人名称
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