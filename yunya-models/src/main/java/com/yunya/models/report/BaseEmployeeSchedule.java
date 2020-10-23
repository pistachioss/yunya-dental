package com.yunya.models.report;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "base_employee_schedule")
public class BaseEmployeeSchedule {
    /**
     * 员工排班ID
     */
    @Column(name = "employee_schedule_id")
    private Integer employeeScheduleId;

    /**
     * 组织ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 排班id
     */
    @Column(name = "schedule_id")
    private Integer scheduleId;

    /**
     * 员工ID
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 排班日期
     */
    @Column(name = "schedule_date")
    private Date scheduleDate;

    /**
     * 上班时间
     */
    @Column(name = "start_work_time")
    private Date startWorkTime;

    /**
     * 下班时间
     */
    @Column(name = "off_work_time")
    private Date offWorkTime;

    /**
     * 排班时长(分钟)
     */
    @Column(name = "schedule_duration")
    private Integer scheduleDuration;

    /**
     * 获取员工排班ID
     *
     * @return employee_schedule_id - 员工排班ID
     */
    public Integer getEmployeeScheduleId() {
        return employeeScheduleId;
    }

    /**
     * 设置员工排班ID
     *
     * @param employeeScheduleId 员工排班ID
     */
    public void setEmployeeScheduleId(Integer employeeScheduleId) {
        this.employeeScheduleId = employeeScheduleId;
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
     * 获取排班id
     *
     * @return schedule_id - 排班id
     */
    public Integer getScheduleId() {
        return scheduleId;
    }

    /**
     * 设置排班id
     *
     * @param scheduleId 排班id
     */
    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
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
     * 获取排班日期
     *
     * @return schedule_date - 排班日期
     */
    public Date getScheduleDate() {
        return scheduleDate;
    }

    /**
     * 设置排班日期
     *
     * @param scheduleDate 排班日期
     */
    public void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    /**
     * 获取上班时间
     *
     * @return start_work_time - 上班时间
     */
    public Date getStartWorkTime() {
        return startWorkTime;
    }

    /**
     * 设置上班时间
     *
     * @param startWorkTime 上班时间
     */
    public void setStartWorkTime(Date startWorkTime) {
        this.startWorkTime = startWorkTime;
    }

    /**
     * 获取下班时间
     *
     * @return off_work_time - 下班时间
     */
    public Date getOffWorkTime() {
        return offWorkTime;
    }

    /**
     * 设置下班时间
     *
     * @param offWorkTime 下班时间
     */
    public void setOffWorkTime(Date offWorkTime) {
        this.offWorkTime = offWorkTime;
    }

    /**
     * 获取排班时长(分钟)
     *
     * @return schedule_duration - 排班时长(分钟)
     */
    public Integer getScheduleDuration() {
        return scheduleDuration;
    }

    /**
     * 设置排班时长(分钟)
     *
     * @param scheduleDuration 排班时长(分钟)
     */
    public void setScheduleDuration(Integer scheduleDuration) {
        this.scheduleDuration = scheduleDuration;
    }
}