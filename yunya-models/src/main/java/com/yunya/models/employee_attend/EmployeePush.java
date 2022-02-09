package com.yunya.models.employee_attend;

import javax.persistence.*;

@Table(name = "employee_push")
public class EmployeePush {
    /**
     * 员工ID：改为使用user_id（员工id）
     */
    @Id
    @Column(name = "employee_id")
    private Integer employeeId;

    /**
     * 员工推送消息的唯一号，此处为极光推送id，由APP取得
     */
    @Column(name = "registration_id")
    private String registrationId;

    /**
     * 1: IOS; 2: Android; 3: QuickApp;
     */
    private Integer platform;

    /**
     * 获取员工ID
     *
     * @return employee_id - 员工ID
     */
    public Integer getEmployeeId() {
        return employeeId;
    }

    /**
     * 设置员工ID
     *
     * @param employeeId 员工ID
     */
    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    /**
     * 获取员工推送消息的唯一号，此处为极光推送id，由APP取得
     *
     * @return registration_id - 员工推送消息的唯一号，此处为极光推送id，由APP取得
     */
    public String getRegistrationId() {
        return registrationId;
    }

    /**
     * 设置员工推送消息的唯一号，此处为极光推送id，由APP取得
     *
     * @param registrationId 员工推送消息的唯一号，此处为极光推送id，由APP取得
     */
    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    /**
     * 获取1: IOS; 2: Android; 3: QuickApp;
     *
     * @return platform - 1: IOS; 2: Android; 3: QuickApp;
     */
    public Integer getPlatform() {
        return platform;
    }

    /**
     * 设置1: IOS; 2: Android; 3: QuickApp;
     *
     * @param platform 1: IOS; 2: Android; 3: QuickApp;
     */
    public void setPlatform(Integer platform) {
        this.platform = platform;
    }
}