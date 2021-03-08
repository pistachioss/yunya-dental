package com.yunya.models.report;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "employee_workload_cost")
public class EmployeeWorkloadCost {
    @Id
    private Integer id;

    /**
     * 员工ID
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 组织ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 录入日期（查询月份）
     */
    @Column(name = "entry_month")
    private Date entryMonth;

    /**
     * 加工费
     */
    @Column(name = "processing_fee")
    private BigDecimal processingFee;

    /**
     * 正畸加工费
     */
    @Column(name = "orthodontics_fee")
    private BigDecimal orthodonticsFee;

    /**
     * 基本工作量
     */
    @Column(name = "base_workload")
    private BigDecimal baseWorkload;

    /**
     * 材料费
     */
    @Column(name = "material_fee")
    private BigDecimal materialFee;

    /**
     * 创建人ID
     */
    @Column(name = "crt_id")
    private Integer crtId;

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
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

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
     * 获取员工ID
     *
     * @return user_id - 员工ID
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置员工ID
     *
     * @param userId 员工ID
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
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
     * 获取录入日期（查询月份）
     *
     * @return entry_month - 录入日期（查询月份）
     */
    public Date getEntryMonth() {
        return entryMonth;
    }

    /**
     * 设置录入日期（查询月份）
     *
     * @param entryMonth 录入日期（查询月份）
     */
    public void setEntryMonth(Date entryMonth) {
        this.entryMonth = entryMonth;
    }

    /**
     * 获取加工费
     *
     * @return processing_fee - 加工费
     */
    public BigDecimal getProcessingFee() {
        return processingFee;
    }

    /**
     * 设置加工费
     *
     * @param processingFee 加工费
     */
    public void setProcessingFee(BigDecimal processingFee) {
        this.processingFee = processingFee;
    }

    /**
     * 获取材料费
     *
     * @return material_fee - 材料费
     */
    public BigDecimal getMaterialFee() {
        return materialFee;
    }

    /**
     * 设置材料费
     *
     * @param materialFee 材料费
     */
    public void setMaterialFee(BigDecimal materialFee) {
        this.materialFee = materialFee;
    }

    public BigDecimal getBaseWorkload() {
        return baseWorkload;
    }

    public BigDecimal getOrthodonticsFee() {
        return orthodonticsFee;
    }

    public void setBaseWorkload(BigDecimal baseWorkload) {
        this.baseWorkload = baseWorkload;
    }

    public void setOrthodonticsFee(BigDecimal orthodonticsFee) {
        this.orthodonticsFee = orthodonticsFee;
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