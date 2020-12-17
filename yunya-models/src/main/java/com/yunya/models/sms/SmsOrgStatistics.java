package com.yunya.models.sms;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "sms_org_statistics")
public class SmsOrgStatistics {
    @Id
    private Integer id;

    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 充值总数
     */
    @Column(name = "charge_num")
    private Integer chargeNum;

    /**
     * 充值总金额
     */
    @Column(name = "charge_money")
    private BigDecimal chargeMoney;

    /**
     * 可用总条数
     */
    @Column(name = "surplus_num")
    private Integer surplusNum;

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
     * 修改人id
     */
    @Column(name = "upt_id")
    private Integer uptId;

    /**
     * 修改时间
     */
    @Column(name = "upt_time")
    private Date uptTime;

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
     * @return org_id
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * @param orgId
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取充值总数
     *
     * @return charge_num - 充值总数
     */
    public Integer getChargeNum() {
        return chargeNum;
    }

    /**
     * 设置充值总数
     *
     * @param chargeNum 充值总数
     */
    public void setChargeNum(Integer chargeNum) {
        this.chargeNum = chargeNum;
    }

    /**
     * 获取充值总金额
     *
     * @return charge_money - 充值总金额
     */
    public BigDecimal getChargeMoney() {
        return chargeMoney;
    }

    /**
     * 设置充值总金额
     *
     * @param chargeMoney 充值总金额
     */
    public void setChargeMoney(BigDecimal chargeMoney) {
        this.chargeMoney = chargeMoney;
    }

    /**
     * 获取可用总条数
     *
     * @return surplus_num - 可用总条数
     */
    public Integer getSurplusNum() {
        return surplusNum;
    }

    /**
     * 设置可用总条数
     *
     * @param surplusNum 可用总条数
     */
    public void setSurplusNum(Integer surplusNum) {
        this.surplusNum = surplusNum;
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