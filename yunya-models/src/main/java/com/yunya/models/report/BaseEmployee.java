package com.yunya.models.report;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "base_employee")
public class BaseEmployee {
    /**
     * 用户ID
     */
    @Id
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 员工姓名
     */
    @Column(name = "employee_name")
    private String employeeName;

    /**
     * 性别（0-男；1-女；2-其他）
     */
    private Byte gender;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 就职状态
     */
    @Column(name = "work_status")
    private Byte workStatus;

    /**
     * 职称
     */
    private String title;

    /**
     * 职级
     */
    @Column(name = "post_level")
    private String postLevel;

    /**
     * 奖金系数
     */
    @Column(name = "bonus_coefficient")
    private Double bonusCoefficient;

    /**
     * 基本工作量
     */
    @Column(name = "work_amount")
    private Double workAmount;

    /**
     * 工号
     */
    @Column(name = "work_number")
    private String workNumber;

    /**
     * 获取用户ID
     *
     * @return user_id - 用户ID
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     *
     * @param userId 用户ID
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取员工姓名
     *
     * @return employee_name - 员工姓名
     */
    public String getEmployeeName() {
        return employeeName;
    }

    /**
     * 设置员工姓名
     *
     * @param employeeName 员工姓名
     */
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    /**
     * 获取性别（0-男；1-女；2-其他）
     *
     * @return gender - 性别（0-男；1-女；2-其他）
     */
    public Byte getGender() {
        return gender;
    }

    /**
     * 设置性别（0-男；1-女；2-其他）
     *
     * @param gender 性别（0-男；1-女；2-其他）
     */
    public void setGender(Byte gender) {
        this.gender = gender;
    }

    /**
     * 获取手机号
     *
     * @return mobile - 手机号
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * 设置手机号
     *
     * @param mobile 手机号
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * 获取就职状态
     *
     * @return work_status - 就职状态
     */
    public Byte getWorkStatus() {
        return workStatus;
    }

    /**
     * 设置就职状态
     *
     * @param workStatus 就职状态
     */
    public void setWorkStatus(Byte workStatus) {
        this.workStatus = workStatus;
    }

    /**
     * 获取职称
     *
     * @return title - 职称
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置职称
     *
     * @param title 职称
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取职级
     *
     * @return post_level - 职级
     */
    public String getPostLevel() {
        return postLevel;
    }

    /**
     * 设置职级
     *
     * @param postLevel 职级
     */
    public void setPostLevel(String postLevel) {
        this.postLevel = postLevel;
    }

    /**
     * 获取奖金系数
     *
     * @return bonus_coefficient - 奖金系数
     */
    public Double getBonusCoefficient() {
        return bonusCoefficient;
    }

    /**
     * 设置奖金系数
     *
     * @param bonusCoefficient 奖金系数
     */
    public void setBonusCoefficient(Double bonusCoefficient) {
        this.bonusCoefficient = bonusCoefficient;
    }

    /**
     * 获取基本工作量
     *
     * @return work_amount - 基本工作量
     */
    public Double getWorkAmount() {
        return workAmount;
    }

    /**
     * 设置基本工作量
     *
     * @param workAmount 基本工作量
     */
    public void setWorkAmount(Double workAmount) {
        this.workAmount = workAmount;
    }

    /**
     * 获取工号
     *
     * @return work_number - 工号
     */
    public String getWorkNumber() {
        return workNumber;
    }

    /**
     * 设置工号
     *
     * @param workNumber 工号
     */
    public void setWorkNumber(String workNumber) {
        this.workNumber = workNumber;
    }
}