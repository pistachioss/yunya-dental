package com.yunya.models.appointment;

import lombok.Data;

import java.util.Date;
import javax.persistence.*;

@Table(name = "online_appoint_item")
@Data
public class OnlineAppointItem {
    /**
     * 主键
     */
    @Id
    @Column(name = "item_id")
    private Integer itemId;

    /**
     * 申请预约项目名称
     */
    private String name;

    /**
     * 预约时长(单位: 分钟)
     */
    private Integer duration;

    /**
     * 是否可用 0-不可以；1-可用
     */
    private Boolean inservice;

    /**
     * 创建人ID
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建人
     */
    @Column(name = "crt_name")
    private String crtName;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人ID
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 更新人ID
     */
    @Column(name = "upd_name")
    private String updName;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 获取主键
     *
     * @return itemId - 主键
     */
    public Integer getItemId() {
        return itemId;
    }

    /**
     * 设置主键
     *
     * @param itemId 主键
     */
    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    /**
     * 获取申请预约项目名称
     *
     * @return name - 申请预约项目名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置申请预约项目名称
     *
     * @param name 申请预约项目名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取是否可用 0-不可以；1-可用
     *
     * @return inservice - 是否可用 0-不可以；1-可用
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否可用 0-不可以；1-可用
     *
     * @param inservice 是否可用 0-不可以；1-可用
     */
    public void setInservice(Boolean inservice) {
        this.inservice = inservice;
    }

    /**
     * 获取创建人ID
     *
     * @return crt_id - 创建人ID
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置创建人ID
     *
     * @param crtId 创建人ID
     */
    public void setCrtId(Integer crtId) {
        this.crtId = crtId;
    }

    /**
     * 获取创建人
     *
     * @return crt_name - 创建人
     */
    public String getCrtName() {
        return crtName;
    }

    /**
     * 设置创建人
     *
     * @param crtName 创建人
     */
    public void setCrtName(String crtName) {
        this.crtName = crtName;
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
     * 获取更新人ID
     *
     * @return upd_id - 更新人ID
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * 设置更新人ID
     *
     * @param updId 更新人ID
     */
    public void setUpdId(Integer updId) {
        this.updId = updId;
    }

    /**
     * 获取更新人ID
     *
     * @return upd_name - 更新人ID
     */
    public String getUpdName() {
        return updName;
    }

    /**
     * 设置更新人ID
     *
     * @param updName 更新人ID
     */
    public void setUpdName(String updName) {
        this.updName = updName;
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

    /**
     * 获取预约时长(单位: 分钟)
     * @return 预约时长(单位: 分钟)
     */
    public Integer getDuration() {
        return duration;
    }

    /**
     * 设置预约时长(单位: 分钟)
     * @param duration 预约时长(单位: 分钟)
     */
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}