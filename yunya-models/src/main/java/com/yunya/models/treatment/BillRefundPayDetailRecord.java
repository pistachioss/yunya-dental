package com.yunya.models.treatment;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "bill_refund_pay_detail_record")
public class BillRefundPayDetailRecord {
    /**
     * 主键
     */
    @Id
    private Integer id;

    /**
     * 退费记录ID
     */
    @Column(name = "bill_refund_record_id")
    private Integer billRefundRecordId;

    /**
     * 退费支付方式ID
     */
    @Column(name = "account_item_id")
    private Integer accountItemId;

    /**
     * 退费支付金额
     */
    @Column(name = "refund_pay_amount")
    private BigDecimal refundPayAmount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否有效
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
     * 获取主键
     *
     * @return id - 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取退费记录ID
     *
     * @return bill_refund_record_id - 退费记录ID
     */
    public Integer getBillRefundRecordId() {
        return billRefundRecordId;
    }

    /**
     * 设置退费记录ID
     *
     * @param billRefundRecordId 退费记录ID
     */
    public void setBillRefundRecordId(Integer billRefundRecordId) {
        this.billRefundRecordId = billRefundRecordId;
    }

    /**
     * 获取退费支付方式ID
     *
     * @return account_item_id - 退费支付方式ID
     */
    public Integer getAccountItemId() {
        return accountItemId;
    }

    /**
     * 设置退费支付方式ID
     *
     * @param accountItemId 退费支付方式ID
     */
    public void setAccountItemId(Integer accountItemId) {
        this.accountItemId = accountItemId;
    }

    /**
     * 获取退费支付金额
     *
     * @return refund_pay_amount - 退费支付金额
     */
    public BigDecimal getRefundPayAmount() {
        return refundPayAmount;
    }

    /**
     * 设置退费支付金额
     *
     * @param refundPayAmount 退费支付金额
     */
    public void setRefundPayAmount(BigDecimal refundPayAmount) {
        this.refundPayAmount = refundPayAmount;
    }

    /**
     * 获取备注
     *
     * @return remark 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取是否有效
     *
     * @return inservice - 是否有效
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否有效
     *
     * @param inservice 是否有效
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