package com.yunya.models.employee_attend;

import java.util.Date;
import javax.persistence.*;

@Table(name = "approval_criteria")
public class ApprovalCriteria {
    @Id
    private Integer id;

    /**
     * 请假跨度起始天数
     */
    @Column(name = "start_day")
    private Integer startDay;

    /**
     * 请假跨度结束天数
     */
    @Column(name = "end_day")
    private Integer endDay;

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
     * 获取请假跨度起始天数
     *
     * @return start_day - 请假跨度起始天数
     */
    public Integer getStartDay() {
        return startDay;
    }

    /**
     * 设置请假跨度起始天数
     *
     * @param startDay 请假跨度起始天数
     */
    public void setStartDay(Integer startDay) {
        this.startDay = startDay;
    }

    /**
     * 获取请假跨度结束天数
     *
     * @return end_day - 请假跨度结束天数
     */
    public Integer getEndDay() {
        return endDay;
    }

    /**
     * 设置请假跨度结束天数
     *
     * @param endDay 请假跨度结束天数
     */
    public void setEndDay(Integer endDay) {
        this.endDay = endDay;
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