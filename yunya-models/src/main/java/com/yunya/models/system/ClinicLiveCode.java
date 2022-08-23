package com.yunya.models.system;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "clinic_live_code")
public class ClinicLiveCode {
    @Id
    private Integer id;

    /**
     * 门诊id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 活码url
     */
    private String qrcode;

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

    /**获取门诊id
     *
     * @return org_id
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置门诊id
     *
     * @param orgId 门诊id
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取活码url
     *
     * @return qrcode - 活码url
     */
    public String getQrcode() {
        return qrcode;
    }

    /**
     * 设置活码url
     *
     * @param qrcode 活码url
     */
    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
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
}