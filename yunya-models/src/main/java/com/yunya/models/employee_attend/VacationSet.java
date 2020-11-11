package com.yunya.models.employee_attend;

import java.util.Date;
import javax.persistence.*;

@Table(name = "vacation_set")
public class VacationSet {
    @Id
    private Integer id;

    /**
     * 申请类型 0：小时 1：班次 2：天
     */
    @Column(name = "vacation_status")
    private Integer vacationStatus;

    /**
     * 假期名称
     */
    @Column(name = "vacation_name")
    private String vacationName;

    /**
     * 适用范围 0：在职员工 1:正式员工 2：试用期员工
     */
    @Column(name = "vacation_range")
    private Integer vacationRange;

    /**
     * 启用状态 0否 1是
     */
    @Column(name = "vacation_enable")
    private Integer vacationEnable;

    /**
     * 假期说明
     */
    @Column(name = "vacation_explain")
    private String vacationExplain;

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
     * 获取申请类型 0：小时 1：班次 2：天
     *
     * @return vacation_status - 申请类型 0：小时 1：班次 2：天
     */
    public Integer getVacationStatus() {
        return vacationStatus;
    }

    /**
     * 设置申请类型 0：小时 1：班次 2：天
     *
     * @param vacationStatus 申请类型 0：小时 1：班次 2：天
     */
    public void setVacationStatus(Integer vacationStatus) {
        this.vacationStatus = vacationStatus;
    }

    /**
     * 获取假期名称
     *
     * @return vacation_name - 假期名称
     */
    public String getVacationName() {
        return vacationName;
    }

    /**
     * 设置假期名称
     *
     * @param vacationName 假期名称
     */
    public void setVacationName(String vacationName) {
        this.vacationName = vacationName;
    }

    /**
     * 获取适用范围 0：在职员工 1:正式员工 2：试用期员工
     *
     * @return vacation_range - 适用范围 0：在职员工 1:正式员工 2：试用期员工
     */
    public Integer getVacationRange() {
        return vacationRange;
    }

    /**
     * 设置适用范围 0：在职员工 1:正式员工 2：试用期员工
     *
     * @param vacationRange 适用范围 0：在职员工 1:正式员工 2：试用期员工
     */
    public void setVacationRange(Integer vacationRange) {
        this.vacationRange = vacationRange;
    }

    /**
     * 获取启用状态 0否 1是
     *
     * @return vacation_enable - 启用状态 0否 1是
     */
    public Integer getVacationEnable() {
        return vacationEnable;
    }

    /**
     * 设置启用状态 0否 1是
     *
     * @param vacationEnable 启用状态 0否 1是
     */
    public void setVacationEnable(Integer vacationEnable) {
        this.vacationEnable = vacationEnable;
    }

    /**
     * 获取假期说明
     *
     * @return vacation_explain - 假期说明
     */
    public String getVacationExplain() {
        return vacationExplain;
    }

    /**
     * 设置假期说明
     *
     * @param vacationExplain 假期说明
     */
    public void setVacationExplain(String vacationExplain) {
        this.vacationExplain = vacationExplain;
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