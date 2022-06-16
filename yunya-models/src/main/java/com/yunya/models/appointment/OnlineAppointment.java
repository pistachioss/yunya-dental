package com.yunya.models.appointment;

import lombok.Data;

import java.util.Date;
import javax.persistence.*;

@Table(name = "online_appointment")
@Data
public class OnlineAppointment {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 门诊ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 医生ID
     */
    @Column(name = "dentist_id")
    private Integer dentistId;

    /**
     * 患者ID,可能为空
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 微信openID，微信用户唯一标识
     */
    @Column(name = "open_id")
    private String openId;

    /**
     * 预约项目ID
     */
    @Column(name = "appoint_item_id")
    private Integer appointItemId;

    /**
     * 预约内容
     */
    @Column(name = "appoint_content")
    private String appointContent;

    /**
     * 预约日期
     */
    @Column(name = "appoint_date")
    private Date appointDate;

    /**
     * 预约时间
     */
    @Column(name = "appoint_time")
    private Date appointTime;

    /**
     * 预约时长
     */
    private Integer duration;

    /**
     * 就诊患者名字
     */
    @Column(name = "patient_name")
    private String patientName;

    /**
     * 患者手机号
     */
    @Column(name = "patient_phone")
    private String patientPhone;

    /**
     * 预约申请状态 0-申请中；1-通过；2-取消
     */
    private Byte status;

    /**
     * 是否有效，是否删除(默认有效) 1-有效；0删除
     */
    private Boolean inservice;
    /**
     * 微信unionId，微信用户唯一标识
     */
    @Column(name = "union_id")
    private String unionId;
    /**
     * 预约来源  0公众号 1小程序
     */
    @Column(name = "wx_type")
    private Integer wxType;
    /**
     * 预约时间 0上午 1下午
     */
    @Column(name = "appoint_ma")
    private Integer appointMa;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;
    /**
     * 创建人名称
     */
    @Column(name = "crt_name")
    private String crtName;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人名称
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
     * 获取门诊ID
     *
     * @return org_id - 门诊ID
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置门诊ID
     *
     * @param orgId 门诊ID
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取医生ID
     *
     * @return dentist_id - 医生ID
     */
    public Integer getDentistId() {
        return dentistId;
    }

    /**
     * 设置医生ID
     *
     * @param dentistId 医生ID
     */
    public void setDentistId(Integer dentistId) {
        this.dentistId = dentistId;
    }

    /**
     * 获取患者ID,可能为空
     *
     * @return patient_id - 患者ID,可能为空
     */
    public Integer getPatientId() {
        return patientId;
    }

    /**
     * 设置患者ID,可能为空
     *
     * @param patientId 患者ID,可能为空
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * 获取预约项目ID
     *
     * @return appoint_item_id - 预约项目ID
     */
    public Integer getAppointItemId() {
        return appointItemId;
    }

    /**
     * 设置预约项目ID
     *
     * @param appointItemId 预约项目ID
     */
    public void setAppointItemId(Integer appointItemId) {
        this.appointItemId = appointItemId;
    }

    /**
     * 获取预约内容
     *
     * @return appointContent - 预约内容
     */
    public String getAppointContent() {
        return appointContent;
    }

    /**
     * 设置预约内容
     *
     * @param appointContent 预约内容
     */
    public void setAppointContent(String appointContent) {
        this.appointContent = appointContent;
    }

    /**
     * 获取预约日期
     *
     * @return appointDate - 预约日期
     */
    public Date getAppointDate() {
        return appointDate;
    }

    /**
     * 设置预约日期
     *
     * @param appointDate 预约日期
     */
    public void setAppointDate(Date appointDate) {
        this.appointDate = appointDate;
    }

    /**
     * 获取预约时间
     *
     * @return appointTime - 预约时间
     */
    public Date getAppointTime() {
        return appointTime;
    }

    /**
     * 设置预约时间
     *
     * @param appointTime 预约时间
     */
    public void setAppointTime(Date appointTime) {
        this.appointTime = appointTime;
    }

    /**
     * 获取预约时长
     * @return 预约时长
     */
    public Integer getDuration() {
        return duration;
    }

    /**
     * 设置预约时长
     * @param duration 预约时长
     */
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    /**
     * 获取就诊患者名字
     *
     * @return patient_name - 就诊患者名字
     */
    public String getPatientName() {
        return patientName;
    }

    /**
     * 设置就诊患者名字
     *
     * @param patientName 就诊患者名字
     */
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    /**
     * 获取患者手机号
     *
     * @return patient_phone - 患者手机号
     */
    public String getPatientPhone() {
        return patientPhone;
    }

    /**
     * 设置患者手机号
     *
     * @param patientPhone 患者手机号
     */
    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    /**
     * 获取预约申请状态 0-申请中；1-通过；2-取消
     *
     * @return status - 预约申请状态 0-申请中；1-通过；2-取消
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置预约申请状态 0-申请中；1-通过；2-取消
     *
     * @param status 预约申请状态 0-申请中；1-通过；2-取消
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * 获取是否有效，是否删除(默认有效) 1-有效；0删除
     *
     * @return inservice - 是否有效，是否删除(默认有效) 1-有效；0删除
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否有效，是否删除(默认有效) 1-有效；0删除
     *
     * @param inservice 是否有效，是否删除(默认有效) 1-有效；0删除
     */
    public void setInservice(Boolean inservice) {
        this.inservice = inservice;
    }

    /**
     * 获取创建人名称
     *
     * @return crt_name - 创建人名称
     */
    public String getCrtName() {
        return crtName;
    }

    /**
     * 设置创建人名称
     *
     * @param crtName 创建人名称
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
     * 获取更新人名称
     *
     * @return upd_name - 更新人名称
     */
    public String getUpdName() {
        return updName;
    }

    /**
     * 设置更新人名称
     *
     * @param updName 更新人名称
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
     * 获取微信openID，微信用户唯一标识
     * @return 微信openID，微信用户唯一标识
     */
    public String getOpenId() {
        return openId;
    }

    /**
     * 设置微信openID，微信用户唯一标识
     * @param openId 微信openID，微信用户唯一标识
     */
    public void setOpenId(String openId) {
        this.openId = openId;
    }
}