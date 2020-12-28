package com.yunya.models.employee_attend;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "attendance_punch_record")
public class AttendancePunchRecord {
    @Id
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    /**
     * 组织id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 班次名称（或者请假、或者外勤、或者加班）
     */
    @Column(name = "name")
    private String name;

    /**
     * 打卡日期
     */
    @Column(name = "punch_date")
    private Date punchDate;

    /**
     * 打卡时间
     */
    @Column(name = "punch_time")
    private Date punchTime;

    /**
     * 打卡类型 0：上班 1：下班
     */
    @Column(name = "punch_type")
    private Byte punchType;

    /**
     * 打卡状态 0:上班正常 1：迟到  2:下班正常  3:早退  4:无效卡
     */
    @Column(name = "punch_status")
    private Byte punchStatus;

    /**
     * 考勤项目来源: 0：上班班次； 1：休息班次；2：按天请假； 3：按班次请假；4：加班；5：外勤
     */
    private Byte source;

    /**
     * 考勤地址ID
     */
    @Column(name = "attendance_address_id")
    private Integer attendanceAddressId;

    /**
     * 打卡经度
     */
    private String longitude;

    /**
     * 打卡纬度
     */
    private String latitude;

    /**
     * 打卡地址（或者wifi名称）
     */
    @Column(name = "punch_address")
    private String punchAddress;

    /**
     * 打卡考勤项目来源: 0：上班班次； 1：休息班次；2：按天请假； 3：按班次请假；4：加班；5：外勤
     */
    @Column(name = "source_id")
    private Integer sourceId;

    /**
     * 员工排班id，按天请假、外勤时为空、加班时为班次模板id
     */
    @Column(name = "es_id")
    private Integer esId;

    /**
     * 开始时间
     */
    @Column(name = "start_time")
    private Date startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time")
    private Date endTime;

    /**
     * 是否打卡：0-否，1-是
     */
    @Column(name = "is_punch")
    private Byte isPunch;

    /**
     * 是否在有效时间范围内打卡：0-否，1-是
     */
    @Column(name = "is_in_scope")
    private Byte isInScope;

    /**
     * wifi打卡的mac地址
     */
    @Column(name = "wifi_mac_address")
    private String wifiMacAddress;

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
     * 设置是否在有效时间范围内打卡：0-否，1-是
     *
     * @param isInScope 是否在有效时间范围内打卡：0-否，1-是
     */
    public void setIsInScope(Byte isInScope) {
        this.isInScope = isInScope;
    }

    /**
     * 获取是否在有效时间范围内打卡：0-否，1-是
     *
     * @return
     */
    public Byte getIsInScope() {
        return isInScope;
    }

    /**
     * 设置员工排班id，按天请假、外勤时为空、加班时为班次模板id
     *
     * @param esId 员工排班id，按天请假、外勤时为空、加班时为班次模板id
     */
    public void setEsId(Integer esId) {
        this.esId = esId;
    }

    /**
     * 获取员工排班id，按天请假、外勤时为空、加班时为班次模板id
     *
     * @return
     */
    public Integer getEsId() {
        return esId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

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
     * @return user_id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * @param userId
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取打卡日期
     *
     * @return punch_date - 打卡日期
     */
    public Date getPunchDate() {
        return punchDate;
    }

    /**
     * 设置打卡日期
     *
     * @param punchDate 打卡日期
     */
    public void setPunchDate(Date punchDate) {
        this.punchDate = punchDate;
    }

    /**
     * 获取打卡时间
     *
     * @return punch_time - 打卡时间
     */
    public Date getPunchTime() {
        return punchTime;
    }

    /**
     * 设置打卡时间
     *
     * @param punchTime 打卡时间
     */
    public void setPunchTime(Date punchTime) {
        this.punchTime = punchTime;
    }

    /**
     * 获取打卡类型 0：上班 1：下班
     *
     * @return punch_type - 打卡类型 0：上班 1：下班
     */
    public Byte getPunchType() {
        return punchType;
    }

    /**
     * 设置打卡类型 0：上班 1：下班
     *
     * @param punchType 打卡类型 0：上班 1：下班
     */
    public void setPunchType(Byte punchType) {
        this.punchType = punchType;
    }

    /**
     * 获取打卡状态 0:上班正常 1：迟到  2:下班正常  3:早退  4:无效卡
     *
     * @return punch_status - 打卡状态 0:上班正常 1：迟到  2:下班正常  3:早退  4:无效卡
     */
    public Byte getPunchStatus() {
        return punchStatus;
    }

    /**
     * 设置打卡状态 0:上班正常 1：迟到  2:下班正常  3:早退  4:无效卡
     *
     * @param punchStatus 打卡状态 0:上班正常 1：迟到  2:下班正常  3:早退  4:无效卡
     */
    public void setPunchStatus(Byte punchStatus) {
        this.punchStatus = punchStatus;
    }

    /**
     * 获取考勤项目来源: 0：上班班次； 1：休息班次；2：按天请假； 3：按班次请假；4：加班；5：外勤
     *
     * @return source - 考勤项目来源: 0：上班班次； 1：休息班次；2：按天请假； 3：按班次请假；4：加班；5：外勤
     */
    public Byte getSource() {
        return source;
    }

    /**
     * 设置考勤项目来源: 0：上班班次； 1：休息班次；2：按天请假； 3：按班次请假；4：加班；5：外勤
     *
     * @param source 考勤项目来源: 0：上班班次； 1：休息班次；2：按天请假； 3：按班次请假；4：加班；5：外勤
     */
    public void setSource(Byte source) {
        this.source = source;
    }

    /**
     * 获取考勤地址ID
     *
     * @return attendance_address_id - 考勤地址ID
     */
    public Integer getAttendanceAddressId() {
        return attendanceAddressId;
    }

    /**
     * 设置考勤地址ID
     *
     * @param attendanceAddressId 考勤地址ID
     */
    public void setAttendanceAddressId(Integer attendanceAddressId) {
        this.attendanceAddressId = attendanceAddressId;
    }

    /**
     * 获取打卡经度
     *
     * @return longitude - 打卡经度
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * 设置打卡经度
     *
     * @param longitude 打卡经度
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * 获取打卡纬度
     *
     * @return latitude - 打卡纬度
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * 设置打卡纬度
     *
     * @param latitude 打卡纬度
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * 获取打卡地址（或者wifi名称）
     *
     * @return punch_address - 打卡地址（或者wifi名称）
     */
    public String getPunchAddress() {
        return punchAddress;
    }

    /**
     * 设置打卡地址（或者wifi名称）
     *
     * @param punchAddress 打卡地址（或者wifi名称）
     */
    public void setPunchAddress(String punchAddress) {
        this.punchAddress = punchAddress;
    }

    /**
     * 获取打卡项目来源：员工排班id、请假id、加班id、外勤id
     *
     * @return source_id - 打卡项目来源：员工排班id、请假id、加班id、外勤id
     */
    public Integer getSourceId() {
        return sourceId;
    }

    /**
     * 设置打卡项目来源：员工排班id、请假id、加班id、外勤id
     *
     * @param sourceId 打卡项目来源：员工排班id、请假id、加班id、外勤id
     */
    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    /**
     * 获取是否打卡：0-否，1-是
     *
     * @return is_punch - 是否打卡：0-否，1-是
     */
    public Byte getIsPunch() {
        return isPunch;
    }

    /**
     * 设置是否打卡：0-否，1-是
     *
     * @param isPunch 是否打卡：0-否，1-是
     */
    public void setIsPunch(Byte isPunch) {
        this.isPunch = isPunch;
    }

    /**
     * 获取wifi打卡的mac地址
     *
     * @return wifi_mac_address - wifi打卡的mac地址
     */
    public String getWifiMacAddress() {
        return wifiMacAddress;
    }

    /**
     * 设置wifi打卡的mac地址
     *
     * @param wifiMacAddress wifi打卡的mac地址
     */
    public void setWifiMacAddress(String wifiMacAddress) {
        this.wifiMacAddress = wifiMacAddress;
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