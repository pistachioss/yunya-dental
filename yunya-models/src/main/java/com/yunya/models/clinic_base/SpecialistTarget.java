package com.yunya.models.clinic_base;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "specialist_target")
public class SpecialistTarget {
    /**
     * 主键id
     */
    @Id
    private Integer id;

    /**
     * 专科项目id
     */
    @Column(name = "ssp_id")
    private Integer sspId;

    /**
     * 目标
     */
    private BigDecimal target;

    /**
     * 日期
     */
    private Date date;

    /**
     * 日期类型
     */
    @Column(name = "date_type")
    private Integer dateType;

    /**
     * 团队类型
     */
    @Column(name = "team_type")
    private String teamType;

    /**
     * 门诊id或者用户id不等
     */
    @Column(name = "num_id")
    private Integer numId;

    /**
     * 创建人id
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 修改id
     */
    @Column(name = "upt_id")
    private Integer uptId;

    /**
     * 修改时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 获取主键id
     *
     * @return id - 主键id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键id
     *
     * @param id 主键id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取专科项目id
     *
     * @return ssp_id - 专科项目id
     */
    public Integer getSspId() {
        return sspId;
    }

    /**
     * 设置专科项目id
     *
     * @param sspId 专科项目id
     */
    public void setSspId(Integer sspId) {
        this.sspId = sspId;
    }

    /**
     * 获取目标
     *
     * @return target - 目标
     */
    public BigDecimal getTarget() {
        return target;
    }

    /**
     * 设置目标
     *
     * @param target 目标
     */
    public void setTarget(BigDecimal target) {
        this.target = target;
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
     * 获取日期类型
     *
     * @return date_type - 日期类型
     */
    public Integer getDateType() {
        return dateType;
    }

    /**
     * 设置日期类型
     *
     * @param dateType 日期类型
     */
    public void setDateType(Integer dateType) {
        this.dateType = dateType;
    }

    /**
     * 获取团队类型
     *
     * @return team_type - 团队类型
     */
    public String getTeamType() {
        return teamType;
    }

    /**
     * 设置团队类型
     *
     * @param teamType 团队类型
     */
    public void setTeamType(String teamType) {
        this.teamType = teamType;
    }

    /**
     * 获取门诊id或者用户id不等
     *
     * @return numId - 门诊id或者用户id不等
     */
    public Integer getNumId() {
        return numId;
    }

    /**
     * 设置门诊id或者用户id不等
     *
     * @param numid 门诊id或者用户id不等
     */
    public void setNumid(Integer numId) {
        this.numId = numId;
    }

    /**
     * 获取创建人id
     *
     * @return crt_id - 创建人id
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置创建人id
     *
     * @param crtId 创建人id
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
     * 获取修改id
     *
     * @return upt_id - 修改id
     */
    public Integer getUptId() {
        return uptId;
    }

    /**
     * 设置修改id
     *
     * @param uptId 修改id
     */
    public void setUptId(Integer uptId) {
        this.uptId = uptId;
    }

    /**
     * 获取修改时间
     *
     * @return upd_time - 修改时间
     */
    public Date getUpdTime() {
        return updTime;
    }

    /**
     * 设置修改时间
     *
     * @param updTime 修改时间
     */
    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }
}