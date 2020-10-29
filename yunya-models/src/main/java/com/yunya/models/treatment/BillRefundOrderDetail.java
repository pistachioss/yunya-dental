package com.yunya.models.treatment;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "bill_refund_order_detail")
public class BillRefundOrderDetail {
    /**
     * 退费项目明细ID
     */
    @Id
    private Integer id;

    /**
     * 组织ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 退费记录ID
     */
    @Column(name = "refund_record_id")
    private Integer refundRecordId;

    /**
     * 开单明细ID
     */
    @Column(name = "order_detail_id")
    private Integer orderDetailId;

    /**
     * 退费金额
     */
    @Column(name = "refund_amout")
    private BigDecimal refundAmout;

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
     * 获取退费项目明细ID
     *
     * @return id - 退费项目明细ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置退费项目明细ID
     *
     * @param id 退费项目明细ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取组织ID
     *
     * @return org_id - 组织ID
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置组织ID
     *
     * @param orgId 组织ID
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取退费记录ID
     *
     * @return refund_record_id - 退费记录ID
     */
    public Integer getRefundRecordId() {
        return refundRecordId;
    }

    /**
     * 设置退费记录ID
     *
     * @param refundRecordId 退费记录ID
     */
    public void setBillRefundRecordId(Integer refundRecordId) {
        this.refundRecordId = refundRecordId;
    }

    /**
     * 获取开单明细ID
     *
     * @return order_detail_id - 开单明细ID
     */
    public Integer getOrderDetailId() {
        return orderDetailId;
    }

    /**
     * 设置开单明细ID
     *
     * @param orderDetailId 开单明细ID
     */
    public void setOrderDetailId(Integer orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    /**
     * 获取退费金额
     *
     * @return refund_amout - 退费金额
     */
    public BigDecimal getRefundAmout() {
        return refundAmout;
    }

    /**
     * 设置退费金额
     *
     * @param refundAmout 退费金额
     */
    public void setRefundAmout(BigDecimal refundAmout) {
        this.refundAmout = refundAmout;
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