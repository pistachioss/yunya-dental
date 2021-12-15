package com.yunya.models.report;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "stat_emp_pay")
public class StatEmpPay {
    /**
     * 门诊id
     */
    @Id
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 医生id
     */
    @Id
    @Column(name = "dentist_id")
    private Integer dentistId;

    /**
     * 项目id
     */
    @Id
    @Column(name = "item_id")
    private Integer itemId;

    /**
     * 项目类型：0-价目，1-商品
     */
    @Id
    @Column(name = "item_type")
    private Byte itemType;

    /**
     * 收费时间
     */
    @Id
    @Column(name = "pay_date")
    private Integer payDate;

    /**
     * 实收工作量
     */
    @Column(name = "received_workload")
    private BigDecimal receivedWorkload;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 创建人
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 获取门诊id
     *
     * @return org_id - 门诊id
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
     * 获取医生id
     *
     * @return dentist_id - 医生id
     */
    public Integer getDentistId() {
        return dentistId;
    }

    /**
     * 设置医生id
     *
     * @param dentistId 医生id
     */
    public void setDentistId(Integer dentistId) {
        this.dentistId = dentistId;
    }

    /**
     * 获取项目id
     *
     * @return item_id - 项目id
     */
    public Integer getItemId() {
        return itemId;
    }

    /**
     * 设置项目id
     *
     * @param itemId 项目id
     */
    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    /**
     * 获取项目类型：0-价目，1-商品
     *
     * @return item_type - 项目类型：0-价目，1-商品
     */
    public Byte getItemType() {
        return itemType;
    }

    /**
     * 设置项目类型：0-价目，1-商品
     *
     * @param itemType 项目类型：0-价目，1-商品
     */
    public void setItemType(Byte itemType) {
        this.itemType = itemType;
    }

    /**
     * 获取收费时间
     *
     * @return pay_date - 收费时间
     */
    public Integer getPayDate() {
        return payDate;
    }

    /**
     * 设置收费时间
     *
     * @param payDate 收费时间
     */
    public void setPayDate(Integer payDate) {
        this.payDate = payDate;
    }

    /**
     * 获取实收工作量
     *
     * @return received_workload - 实收工作量
     */
    public BigDecimal getReceivedWorkload() {
        return receivedWorkload;
    }

    /**
     * 设置实收工作量
     *
     * @param receivedWorkload 实收工作量
     */
    public void setReceivedWorkload(BigDecimal receivedWorkload) {
        this.receivedWorkload = receivedWorkload;
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
     * 获取创建人
     *
     * @return crt_id - 创建人
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置创建人
     *
     * @param crtId 创建人
     */
    public void setCrtId(Integer crtId) {
        this.crtId = crtId;
    }
}