package com.yunya.models.employee_attend;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "attendance_address_set")
public class AttendanceAddressSet {
    @Id
    private Integer id;

    /**
     * 组织Id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 组织名称（前端使用）
     */
    @Column(name = "organization_name")
    private String organizationName;


    /**
     * 考勤地址
     */
    @Column(name = "attendance_address")
    private String attendanceAddress;

    /**
     * 考勤地址经度
     */
    private String longitude;

    /**
     * 考勤地址纬度
     */
    private String latitude;

    /**
     * 考勤范围(米)
     */
    @Column(name = "attendance_range")
    private Integer attendanceRange;

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
     * 更新人
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
     * 获取组织Id
     *
     * @return org_id - 组织Id
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置组织Id
     *
     * @param orgId 组织Id
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取组织名称（前端使用）
     *
     * @return
     */
    public String getOrganizationName() {
        return organizationName;
    }

    /**
     * 设置组织名称（前端使用）
     * @param organizationName 组织名称（前端使用）
     */
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    /**
     * 获取考勤地址
     *
     * @return attendance_address - 考勤地址
     */
    public String getAttendanceAddress() {
        return attendanceAddress;
    }

    /**
     * 设置考勤地址
     *
     * @param attendanceAddress 考勤地址
     */
    public void setAttendanceAddress(String attendanceAddress) {
        this.attendanceAddress = attendanceAddress;
    }

    /**
     * 获取考勤地址经度
     *
     * @return longitude - 考勤地址经度
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * 设置考勤地址经度
     *
     * @param longitude 考勤地址经度
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * 获取考勤地址纬度
     *
     * @return latitude - 考勤地址纬度
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * 设置考勤地址纬度
     *
     * @param latitude 考勤地址纬度
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * 获取考勤范围(米)
     *
     * @return attendance_range - 考勤范围(米)
     */
    public Integer getAttendanceRange() {
        return attendanceRange;
    }

    /**
     * 设置考勤范围(米)
     *
     * @param attendanceRange 考勤范围(米)
     */
    public void setAttendanceRange(Integer attendanceRange) {
        this.attendanceRange = attendanceRange;
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
     * 获取更新人
     *
     * @return upd_id - 更新人
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * 设置更新人
     *
     * @param updId 更新人
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