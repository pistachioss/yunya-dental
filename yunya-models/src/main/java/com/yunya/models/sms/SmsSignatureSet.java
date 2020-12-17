package com.yunya.models.sms;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "sms_signature_set")
public class SmsSignatureSet {
    @Id
    private Integer id;

    /**
     * 组织id（门诊、公司）
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 签名名称
     */
    @Column(name = "sign_name")
    private String signName;

    /**
     * 签名申请说明
     */
    private String remark;

    /**
     * 签名来源。0：企事业单位的全称或简称。1：工信部备案网站的全称或简称。2：APP应用的全称或简称。3：公众号或小程序的全称或简称。
4：电商平台店铺名的全称或简称。
5：商标名的全称或简称
     */
    @Column(name = "sign_source")
    private Byte signSource;

    /**
     * 签名审核状态：0：审核中。1：审核通过。2：审核失败
     */
    @Column(name = "sign_status")
    private Byte signStatus;

    /**
     * 创建人id
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 提交人
     */
    @Column(name = "crt_user")
    private String crtUser;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 修改人id
     */
    @Column(name = "upt_id")
    private Integer uptId;

    /**
     * 修改时间
     */
    @Column(name = "upt_time")
    private Date uptTime;

    public void setCrtUser(String crtUser) {
        this.crtUser = crtUser;
    }

    public String getCrtUser() {
        return crtUser;
    }

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
     * 获取组织id（门诊、公司）
     *
     * @return org_id - 组织id（门诊、公司）
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置组织id（门诊、公司）
     *
     * @param orgId 组织id（门诊、公司）
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取签名名称
     *
     * @return sign_name - 签名名称
     */
    public String getSignName() {
        return signName;
    }

    /**
     * 设置签名名称
     *
     * @param signName 签名名称
     */
    public void setSignName(String signName) {
        this.signName = signName;
    }

    /**
     * 获取签名申请说明
     *
     * @return remark - 签名申请说明
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置签名申请说明
     *
     * @param remark 签名申请说明
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取签名来源。0：企事业单位的全称或简称。1：工信部备案网站的全称或简称。2：APP应用的全称或简称。3：公众号或小程序的全称或简称。
4：电商平台店铺名的全称或简称。
5：商标名的全称或简称
     *
     * @return sign_source - 签名来源。0：企事业单位的全称或简称。1：工信部备案网站的全称或简称。2：APP应用的全称或简称。3：公众号或小程序的全称或简称。
4：电商平台店铺名的全称或简称。
5：商标名的全称或简称
     */
    public Byte getSignSource() {
        return signSource;
    }

    /**
     * 设置签名来源。0：企事业单位的全称或简称。1：工信部备案网站的全称或简称。2：APP应用的全称或简称。3：公众号或小程序的全称或简称。
4：电商平台店铺名的全称或简称。
5：商标名的全称或简称
     *
     * @param signSource 签名来源。0：企事业单位的全称或简称。1：工信部备案网站的全称或简称。2：APP应用的全称或简称。3：公众号或小程序的全称或简称。
4：电商平台店铺名的全称或简称。
5：商标名的全称或简称
     */
    public void setSignSource(Byte signSource) {
        this.signSource = signSource;
    }

    /**
     * 获取签名审核状态：0：审核中。1：审核通过。2：审核失败
     *
     * @return sign_status - 签名审核状态：0：审核中。1：审核通过。2：审核失败
     */
    public Byte getSignStatus() {
        return signStatus;
    }

    /**
     * 设置签名审核状态：0：审核中。1：审核通过。2：审核失败
     *
     * @param signStatus 签名审核状态：0：审核中。1：审核通过。2：审核失败
     */
    public void setSignStatus(Byte signStatus) {
        this.signStatus = signStatus;
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
     * 获取修改人id
     *
     * @return upt_id - 修改人id
     */
    public Integer getUptId() {
        return uptId;
    }

    /**
     * 设置修改人id
     *
     * @param uptId 修改人id
     */
    public void setUptId(Integer uptId) {
        this.uptId = uptId;
    }

    /**
     * 获取修改时间
     *
     * @return upt_time - 修改时间
     */
    public Date getUptTime() {
        return uptTime;
    }

    /**
     * 设置修改时间
     *
     * @param uptTime 修改时间
     */
    public void setUptTime(Date uptTime) {
        this.uptTime = uptTime;
    }
}