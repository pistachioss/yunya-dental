package com.yunya.models.employee_attend;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "attendance_manual_makeup")
public class AttendanceManualMakeup {
    @Id
    private Integer id;

    /**
     * 补入日期
     */
    @Column(name = "makeup_date")
    private Date makeupDate;

    /**
     * 员工id
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 组织id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 手动补入时长（分钟）
     */
    private Integer minute;

    /**
     * 补入说明（手动输入）
     */
    @Column(name = "makeup_desc")
    private String makeupDesc;

    /**
     * 补入类型：0-工作日补入，1-加班补入
     */
    private Byte type;

    /**
     * 创建人
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 修改人
     */
    @Column(name = "upt_id")
    private Integer uptId;

    /**
     * 修改时间
     */
    @Column(name = "upt_time")
    private Date uptTime;

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
     * 获取补入日期
     *
     * @return makeup_date - 补入日期
     */
    public Date getMakeupDate() {
        return makeupDate;
    }

    /**
     * 设置补入日期
     *
     * @param makeupDate 补入日期
     */
    public void setMakeupDate(Date makeupDate) {
        this.makeupDate = makeupDate;
    }

    /**
     * 获取员工id
     *
     * @return user_id - 员工id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置员工id
     *
     * @param userId 员工id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取组织id
     *
     * @return org_id - 组织id
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置组织id
     *
     * @param orgId 组织id
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取手动补入时长（分钟）
     *
     * @return minute - 手动补入时长（分钟）
     */
    public Integer getMinute() {
        return minute;
    }

    /**
     * 设置手动补入时长（分钟）
     *
     * @param minute 手动补入时长（分钟）
     */
    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    /**
     * 补入说明（手动输入）
     * @return
     */
    public String getMakeupDesc() {
        return makeupDesc;
    }

    /**
     * 设置补入说明（手动输入）
     *
     * @param makeupDesc 补入说明（手动输入）
     */
    public void setMakeupDesc(String makeupDesc) {
        this.makeupDesc = makeupDesc;
    }

    /**
     * 获取补入类型：0-工作日补入，1-加班补入
     *
     * @return type - 补入类型：0-工作日补入，1-加班补入
     */
    public Byte getType() {
        return type;
    }

    /**
     * 设置补入类型：0-工作日补入，1-加班补入
     *
     * @param type 补入类型：0-工作日补入，1-加班补入
     */
    public void setType(Byte type) {
        this.type = type;
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
     * 获取修改人
     *
     * @return upt_id - 修改人
     */
    public Integer getUptId() {
        return uptId;
    }

    /**
     * 设置修改人
     *
     * @param uptId 修改人
     */
    public void setUptId(Integer uptId) {
        this.uptId = uptId;
    }

    /**
     * 获取修改时间
     *
     * @return upt_time - 修改时间
     */
    public Date getUptTime() {
        return uptTime;
    }

    /**
     * 设置修改时间
     *
     * @param uptTime 修改时间
     */
    public void setUptTime(Date uptTime) {
        this.uptTime = uptTime;
    }
}