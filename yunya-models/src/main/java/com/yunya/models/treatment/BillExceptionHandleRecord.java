package com.yunya.models.treatment;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "bill_exception_handle_record")
public class BillExceptionHandleRecord {
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 组织ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 患者ID
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 就诊记录ID
     */
    @Column(name = "treatment_record_id")
    private Integer treatmentRecordId;

    /**
     * 操作对象记录ID（与操作类型相关，operate_type-0，该ID为收费方式记录ID）
     */
    @Column(name = "handled_record_id")
    private Integer handledRecordId;

    /**
     * 操作类型（0-收费方式调整；1-撤销收费；2-修改账单；3-账单退费）
     */
    @Column(name = "operate_type")
    private Byte operateType;

    /**
     * 操作备注
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
     * 获取患者ID
     *
     * @return patient_id - 患者ID
     */
    public Integer getPatientId() {
        return patientId;
    }

    /**
     * 设置患者ID
     *
     * @param patientId 患者ID
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * 获取就诊记录ID
     *
     * @return treatment_record_id - 获取就诊记录ID
     */
    public Integer getTreatmentRecordId() {
        return treatmentRecordId;
    }

    /**
     * 设置就诊记录ID
     *
     * @param treatmentRecordId 就诊记录ID
     */
    public void setTreatmentRecordId(Integer treatmentRecordId) {
        this.treatmentRecordId = treatmentRecordId;
    }

    /**
     * 获取操作对象记录ID（与操作类型相关，operate_type-0，该ID为收费方式记录ID）
     *
     * @return handled_record_id - 操作对象记录ID（与操作类型相关，operate_type-0，该ID为收费方式记录ID）
     */
    public Integer getHandledRecordId() {
        return handledRecordId;
    }

    /**
     * 设置操作对象记录ID（与操作类型相关，operate_type-0，该ID为收费方式记录ID）
     *
     * @param handledRecordId 操作对象记录ID（与操作类型相关，operate_type-0，该ID为收费方式记录ID）
     */
    public void setHandledRecordId(Integer handledRecordId) {
        this.handledRecordId = handledRecordId;
    }

    /**
     * 获取操作类型（0-收费方式调整；1-撤销收费；2-修改账单；3-账单退费）
     *
     * @return operate_type - 操作类型（0-收费方式调整；1-撤销收费；2-修改账单；3-账单退费）
     */
    public Byte getOperateType() {
        return operateType;
    }

    /**
     * 设置操作类型（0-收费方式调整；1-撤销收费；2-修改账单；3-账单退费）
     *
     * @param operateType 操作类型（0-收费方式调整；1-撤销收费；2-修改账单；3-账单退费）
     */
    public void setOperateType(Byte operateType) {
        this.operateType = operateType;
    }

    /**
     * 获取操作备注
     *
     * @return remark - 操作备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置操作备注
     *
     * @param remark 操作备注
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