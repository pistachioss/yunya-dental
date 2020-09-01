package com.yunya.models.clinic_base;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "business_target")
public class BusinessTarget {
    @Id
    private Integer id;

    /**
     * 实收金额
     */
    @Column(name = "target_cash")
    private BigDecimal targetCash;

    /**
     * 目标工作量
     */
    @Column(name = "target_num")
    private BigDecimal targetNum;

    /**
     * 目标初诊人数
     */
    @Column(name = "target_first_visit")
    private Integer targetFirstVisit;

    /**
     * 目标就诊人数
     */
    @Column(name = "target_patient_num")
    private Integer targetPatientNum;

    /**
     * 日期类型: 年1 月2
     */
    @Column(name = "date_type")
    private Integer dateType;

    /**
     * 日期
     */
    private Date date;

    /**
     * 组织类型:1 门诊，2个人
     */
    @Column(name = "team_type")
    private String teamType;

    /**
     * 门诊id或者用户id不等
     */
    @Column(name = "num_id")
    private Integer numId;

    @Column(name = "crt_id")
    private Integer crtId;

    @Column(name = "crt_time")
    private Date crtTime;

    @Column(name = "upd_id")
    private Integer updId;

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
     * 获取实收金额
     *
     * @return target_cash - 实收金额
     */
    public BigDecimal getTargetCash() {
        return targetCash;
    }

    /**
     * 设置实收金额
     *
     * @param targetCash 实收金额
     */
    public void setTargetCash(BigDecimal targetCash) {
        this.targetCash = targetCash;
    }

    /**
     * 获取目标工作量
     *
     * @return target_num - 目标工作量
     */
    public BigDecimal getTargetNum() {
        return targetNum;
    }

    /**
     * 设置目标工作量
     *
     * @param targetNum 目标工作量
     */
    public void setTargetNum(BigDecimal targetNum) {
        this.targetNum = targetNum;
    }

    /**
     * 获取目标初诊人数
     *
     * @return target_first_visit - 目标初诊人数
     */
    public Integer getTargetFirstVisit() {
        return targetFirstVisit;
    }

    /**
     * 设置目标初诊人数
     *
     * @param targetFirstVisit 目标初诊人数
     */
    public void setTargetFirstVisit(Integer targetFirstVisit) {
        this.targetFirstVisit = targetFirstVisit;
    }

    /**
     * 获取目标就诊人数
     *
     * @return target_patient_num - 目标就诊人数
     */
    public Integer getTargetPatientNum() {
        return targetPatientNum;
    }

    /**
     * 设置目标就诊人数
     *
     * @param targetPatientNum 目标就诊人数
     */
    public void setTargetPatientNum(Integer targetPatientNum) {
        this.targetPatientNum = targetPatientNum;
    }

    /**
     * 获取日期类型: 年1 月2
     *
     * @return date_type - 日期类型: 年1 月2
     */
    public Integer getDateType() {
        return dateType;
    }

    /**
     * 设置日期类型: 年1 月2
     *
     * @param dateType 日期类型: 年1 月2
     */
    public void setDateType(Integer dateType) {
        this.dateType = dateType;
    }

    /**
     * 获取日期
     *
     * @return date - 日期
     */
    public Date getDate() {
        return date;
    }

    /**
     * 设置日期
     *
     * @param date 日期
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * 获取组织类型:1 门诊，2个人
     *
     * @return team_type - 组织类型:1 门诊，2个人
     */
    public String getTeamType() {
        return teamType;
    }

    /**
     * 设置组织类型:1 门诊，2个人
     *
     * @param teamType 组织类型:1 门诊，2个人
     */
    public void setTeamType(String teamType) {
        this.teamType = teamType;
    }

    /**
     * 获取门诊id或者用户id不等
     *
     * @return num_id - 门诊id或者用户id不等
     */
    public Integer getNumId() {
        return numId;
    }

    /**
     * 设置门诊id或者用户id不等
     *
     * @param numId 门诊id或者用户id不等
     */
    public void setNumId(Integer numId) {
        this.numId = numId;
    }

    /**
     * @return crt_id
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * @param crtId
     */
    public void setCrtId(Integer crtId) {
        this.crtId = crtId;
    }

    /**
     * @return crt_time
     */
    public Date getCrtTime() {
        return crtTime;
    }

    /**
     * @param crtTime
     */
    public void setCrtTime(Date crtTime) {
        this.crtTime = crtTime;
    }

    /**
     * @return upd_id
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * @param updId
     */
    public void setUpdId(Integer updId) {
        this.updId = updId;
    }

    /**
     * @return upd_time
     */
    public Date getUpdTime() {
        return updTime;
    }

    /**
     * @param updTime
     */
    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }
}