package com.yunya.models.report;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "stat_emp_bill")
public class StatEmpBill {
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
     * 项目类型：0-价目，1-商品
     */
    @Id
    @Column(name = "item_type")
    private Byte itemType;

    /**
     * 项目id
     */
    @Id
    @Column(name = "item_id")
    private Integer itemId;

    /**
     * 账单日期
     */
    @Id
    @Column(name = "bill_date")
    private Integer billDate;

    /**
     * 应收工作量
     */
    @Column(name = "receivable_workload")
    private BigDecimal receivableWorkload;

    /**
     * 项目数量
     */
    private Integer quantity;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 创建人id
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
     * 获取账单日期
     *
     * @return bill_date - 账单日期
     */
    public Integer getBillDate() {
        return billDate;
    }

    /**
     * 设置账单日期
     *
     * @param billDate 账单日期
     */
    public void setBillDate(Integer billDate) {
        this.billDate = billDate;
    }

    /**
     * 获取应收工作量
     *
     * @return receivable_workload - 应收工作量
     */
    public BigDecimal getReceivableWorkload() {
        return receivableWorkload;
    }

    /**
     * 设置应收工作量
     *
     * @param receivableWorkload 应收工作量
     */
    public void setReceivableWorkload(BigDecimal receivableWorkload) {
        this.receivableWorkload = receivableWorkload;
    }

    /**
     * 获取项目数量
     *
     * @return quantity - 项目数量
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * 设置项目数量
     *
     * @param quantity 项目数量
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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
}