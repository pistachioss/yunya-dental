package com.yunya.models.report;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "stat_emp_refund")
public class StatEmpRefund {
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
     * 退费时间
     */
    @Id
    @Column(name = "refund_date")
    private Integer refundDate;

    /**
     * 退费工作量
     */
    @Column(name = "refund_workload")
    private BigDecimal refundWorkload;

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
     * 获取退费时间
     *
     * @return refund_date - 退费时间
     */
    public Integer getRefundDate() {
        return refundDate;
    }

    /**
     * 设置退费时间
     *
     * @param refundDate 退费时间
     */
    public void setRefundDate(Integer refundDate) {
        this.refundDate = refundDate;
    }

    /**
     * 获取退费工作量
     *
     * @return refund_workload - 退费工作量
     */
    public BigDecimal getRefundWorkload() {
        return refundWorkload;
    }

    /**
     * 设置退费工作量
     *
     * @param refundWorkload 退费工作量
     */
    public void setRefundWorkload(BigDecimal refundWorkload) {
        this.refundWorkload = refundWorkload;
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