package com.yunya.models.patient_central;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import javax.persistence.*;

@Table(name = "patient_origin")
public class PatientOrigin {
    /**
     * 患者来源ID
     */
    @Id
    private Integer id;

    /**
     * 患者来源父ID（顶级为0）
     */
    @Column(name = "parent_id")
    private Integer parentId;

    /**
     * 患者来源名称
     */
    private String name;

    /**
     * 患者来源类型
     */
    @Column(name = "origin_type")
    private Integer originType;

    /**
     * 二维码地址
     */
    @Column(name = "qr_code_path")
    private String qrCodePath;

    /**
     * 是否允许操作（编辑、删除）
     */
    @Column(name = "allow_operate")
    private Boolean allowOperate;

    /**
     * 是否有时间限制（0-否；1-是））
     */
    @Column(name = "time_limit")
    private Byte timeLimit;

    /**
     * 限制开始时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "limit_start_date")
    private Date limitStartDate;

    /**
     * 限制结束时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "limit_end_date")
    private Date limitEndDate;

    /**
     * 是否启用
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
     * 获取患者来源ID
     *
     * @return id - 患者来源ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置患者来源ID
     *
     * @param id 患者来源ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取患者来源父ID（顶级为0）
     *
     * @return parent_id - 患者来源父ID（顶级为0）
     */
    public Integer getParentId() {
        return parentId;
    }

    /**
     * 设置患者来源父ID（顶级为0）
     *
     * @param parentId 患者来源父ID（顶级为0）
     */
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    /**
     * 获取患者来源名称
     *
     * @return name - 患者来源名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置患者来源名称
     *
     * @param name 患者来源名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取二维码地址
     *
     * @return qr_code_path - 二维码地址
     */
    public String getQrCodePath() {
        return qrCodePath;
    }

    /**
     * 设置二维码地址
     *
     * @param qrCodePath 二维码地址
     */
    public void setQrCodePath(String qrCodePath) {
        this.qrCodePath = qrCodePath;
    }

    /**
     * 获取是否允许操作（编辑、删除）
     *
     * @return allow_operate - 是否允许操作（编辑、删除）
     */
    public Boolean getAllowOperate() {
        return allowOperate;
    }

    /**
     * 设置是否允许操作（编辑、删除）
     *
     * @param allowOperate 是否允许操作（编辑、删除）
     */
    public void setAllowOperate(Boolean allowOperate) {
        this.allowOperate = allowOperate;
    }

    /**
     * 获取是否有时间限制（0-否；1-是））
     *
     * @return time_limit - 是否有时间限制（0-否；1-是））
     */
    public Byte getTimeLimit() {
        return timeLimit;
    }

    /**
     * 设置是否有时间限制（0-否；1-是））
     *
     * @param timeLimit 是否有时间限制（0-否；1-是））
     */
    public void setTimeLimit(Byte timeLimit) {
        this.timeLimit = timeLimit;
    }

    /**
     * 获取限制开始时间
     *
     * @return limit_start_date - 限制开始时间
     */
    public Date getLimitStartDate() {
        return limitStartDate;
    }

    /**
     * 设置限制开始时间
     *
     * @param limitStartDate 限制开始时间
     */
    public void setLimitStartDate(Date limitStartDate) {
        this.limitStartDate = limitStartDate;
    }

    /**
     * 获取限制介绍时间
     *
     * @return limit_end_date - 限制介绍时间
     */
    public Date getLimitEndDate() {
        return limitEndDate;
    }

    /**
     * 设置限制介绍时间
     *
     * @param limitEndDate 限制介绍时间
     */
    public void setLimitEndDate(Date limitEndDate) {
        this.limitEndDate = limitEndDate;
    }

    /**
     * 获取是否启用
     *
     * @return inservice - 是否启用
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否启用
     *
     * @param inservice 是否启用
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

    /**
     * 获取患者来源类型 3活动来源 4合作商来源
     *
     * @return originType
     */
    public Integer getOriginType() {
        return originType;
    }
    /**
     * 设置患者来源类型 3活动来源 4合作商来源
     *
     * @param originType
     */
    public void setOriginType(Integer originType) {
        this.originType = originType;
    }
}