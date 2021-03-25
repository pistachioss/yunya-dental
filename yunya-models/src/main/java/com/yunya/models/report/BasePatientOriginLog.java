package com.yunya.models.report;

import java.util.Date;
import javax.persistence.*;

@Table(name = "base_patient_origin_log")
public class BasePatientOriginLog {
    /**
     * 主键id
     */
    @Id
    private Integer id;

    /**
     * 患者id
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 患者来源类型 1.员工转介绍 2.患者转介绍 3.线下活动 4.合作机构 5.集团客户 6.外院转诊 7.店招 8.网站 9.自媒体 10.搜索 11.团购 
     */
    @Column(name = "origin_type")
    private Integer originType;

    /**
     * 来源id
     */
    @Column(name = "origin_id")
    private Integer originId;

    /**
     * 备注 备注
     */
    private String remarks;

    /**
     * 是否有效 是否有效
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
    @Column(name = "upt_id")
    private Integer uptId;

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
     * 获取主键id
     *
     * @return id - 主键id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键id
     *
     * @param id 主键id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取患者id
     *
     * @return patient_id - 患者id
     */
    public Integer getPatientId() {
        return patientId;
    }

    /**
     * 设置患者id
     *
     * @param patientId 患者id
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * 获取患者来源类型 1.员工转介绍 2.患者转介绍 3.线下活动 4.合作机构 5.集团客户 6.外院转诊 7.店招 8.网站 9.自媒体 10.搜索 11.团购 
     *
     * @return origin_type - 患者来源类型 1.员工转介绍 2.患者转介绍 3.线下活动 4.合作机构 5.集团客户 6.外院转诊 7.店招 8.网站 9.自媒体 10.搜索 11.团购 
     */
    public Integer getOriginType() {
        return originType;
    }

    /**
     * 设置患者来源类型 1.员工转介绍 2.患者转介绍 3.线下活动 4.合作机构 5.集团客户 6.外院转诊 7.店招 8.网站 9.自媒体 10.搜索 11.团购 
     *
     * @param originType 患者来源类型 1.员工转介绍 2.患者转介绍 3.线下活动 4.合作机构 5.集团客户 6.外院转诊 7.店招 8.网站 9.自媒体 10.搜索 11.团购 
     */
    public void setOriginType(Integer originType) {
        this.originType = originType;
    }

    /**
     * 获取来源id
     *
     * @return origin_id - 来源id
     */
    public Integer getOriginId() {
        return originId;
    }

    /**
     * 设置来源id
     *
     * @param originId 来源id
     */
    public void setOriginId(Integer originId) {
        this.originId = originId;
    }

    /**
     * 获取备注 备注
     *
     * @return remarks - 备注 备注
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * 设置备注 备注
     *
     * @param remarks 备注 备注
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * 获取是否有效 是否有效
     *
     * @return inservice - 是否有效 是否有效
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否有效 是否有效
     *
     * @param inservice 是否有效 是否有效
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
     * @return upt_id - 更新人ID
     */
    public Integer getUptId() {
        return uptId;
    }

    /**
     * 设置更新人ID
     *
     * @param uptId 更新人ID
     */
    public void setUptId(Integer uptId) {
        this.uptId = uptId;
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