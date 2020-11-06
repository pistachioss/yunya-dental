package com.yunya.models.employee_attend;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "attendance_device_binding")
public class AttendanceDeviceBinding {
    @Id
    private Integer id;

    /**
     * 绑定用户id
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 绑定状态 0：已解绑 1：已绑定
     */
    @Column(name = "binding_status")
    private Integer bindingStatus;

    /**
     * 绑定时间
     */
    @Column(name = "binding_time")
    private Date bindingTime;

    /**
     * 设备号
     */
    @Column(name = "device_number")
    private String deviceNumber;

    /**
     * 初始化设备号
     */
    @Column(name = "first_number")
    private String firstNumber;

    /**
     * 上次绑定设备号（绑定记录中的原始设备号）
     */
    @Column(name = "old_number")
    private String oldNumber;

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
     * 获取绑定用户id
     *
     * @return user_id - 绑定用户id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置绑定用户id
     *
     * @param userId 绑定用户id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取绑定状态 0：已解绑 1：已绑定
     *
     * @return binding_status - 绑定状态 0：已解绑 1：已绑定
     */
    public Integer getBindingStatus() {
        return bindingStatus;
    }

    /**
     * 设置绑定状态 0：已解绑 1：已绑定
     *
     * @param bindingStatus 绑定状态 0：已解绑 1：已绑定
     */
    public void setBindingStatus(Integer bindingStatus) {
        this.bindingStatus = bindingStatus;
    }

    /**
     * 获取绑定时间
     *
     * @return binding_time - 绑定时间
     */
    public Date getBindingTime() {
        return bindingTime;
    }

    /**
     * 设置绑定时间
     *
     * @param bindingTime 绑定时间
     */
    public void setBindingTime(Date bindingTime) {
        this.bindingTime = bindingTime;
    }

    /**
     * 获取设备号
     *
     * @return device_number - 设备号
     */
    public String getDeviceNumber() {
        return deviceNumber;
    }

    /**
     * 设置设备号
     *
     * @param deviceNumber 设备号
     */
    public void setDeviceNumber(String deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    /**
     * 获取初始化设备号
     *
     * @return first_number - 初始化设备号
     */
    public String getFirstNumber() {
        return firstNumber;
    }

    /**
     * 设置初始化设备号
     *
     * @param firstNumber 初始化设备号
     */
    public void setFirstNumber(String firstNumber) {
        this.firstNumber = firstNumber;
    }

    /**
     * 获取上次绑定设备号（绑定记录中的原始设备号）
     *
     * @return old_number - 上次绑定设备号（绑定记录中的原始设备号）
     */
    public String getOldNumber() {
        return oldNumber;
    }

    /**
     * 设置上次绑定设备号（绑定记录中的原始设备号）
     *
     * @param oldNumber 上次绑定设备号（绑定记录中的原始设备号）
     */
    public void setOldNumber(String oldNumber) {
        this.oldNumber = oldNumber;
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