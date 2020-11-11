package com.yunya.models.treatment;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "bill_exception_handle_detail_record")
public class BillExceptionHandleDetailRecord {
    /**
     * 主键ID
     */
    @Id
    private Integer id;

    /**
     * 账单处理记录ID
     */
    @Column(name = "bill_handle_record_id")
    private Integer billHandleRecordId;

    /**
     * 被处理数据记录ID
     */
    @Column(name = "associate_record_id")
    private Integer associateRecordId;

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
     * 获取主键ID
     *
     * @return id - 主键ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键ID
     *
     * @param id 主键ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取账单处理记录ID
     *
     * @return bill_handle_record_id - 账单处理记录ID
     */
    public Integer getBillHandleRecordId() {
        return billHandleRecordId;
    }

    /**
     * 设置账单处理记录ID
     *
     * @param billHandleRecordId 账单处理记录ID
     */
    public void setBillHandleRecordId(Integer billHandleRecordId) {
        this.billHandleRecordId = billHandleRecordId;
    }

    /**
     * 获取被处理数据记录ID
     *
     * @return associate_record_id - 被处理数据记录ID
     */
    public Integer getAssociateRecordId() {
        return associateRecordId;
    }

    /**
     * 设置被处理数据记录ID
     *
     * @param associateRecordId 被处理数据记录ID
     */
    public void setAssociateRecordId(Integer associateRecordId) {
        this.associateRecordId = associateRecordId;
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