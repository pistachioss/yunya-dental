package com.yunya.models.appointment;

import java.util.Date;
import javax.persistence.*;

@Table(name = "online_appoint_item_setting")
public class OnlineAppointItemSetting {
    /**
     * 主键
     */
    @Id
    @Column(name = "item_setting_id")
    private Integer itemSettingId;

    /**
     * 医生ID
     */
    @Column(name = "dentist_id")
    private Integer dentistId;

    /**
     * 门诊ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 线上预约项目ID，ID之间用“,”隔开
     */
    @Column(name = "enable_appoint_item_ids")
    private String enableAppointItemIds;

    /**
     * 是否删除，是否有效；1-有效，0-无效
     */
    private Boolean inservice;

    /**
     * 创建人ID
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建日期
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 创建人
     */
    @Column(name = "crt_name")
    private String crtName;

    /**
     * 更新人ID
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 更新人名
     */
    @Column(name = "upd_name")
    private String updName;

    /**
     * 更新日期
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 获取主键
     *
     * @return itemSettingId - 主键
     */
    public Integer getItemSettingId() {
        return itemSettingId;
    }

    /**
     * 设置主键
     *
     * @param itemSettingId 主键
     */
    public void setItemSettingId(Integer itemSettingId) {
        this.itemSettingId = itemSettingId;
    }

    /**
     * 获取预约项目ID
     *
     * @return itemId - 预约项目ID
     */
    public String getEnableAppointItemIds() {
        return enableAppointItemIds;
    }

    /**
     * 设置预约项目ID
     *
     * @param enableAppointItemIds 预约项目ID
     */
    public void setEnableAppointItemIds(String enableAppointItemIds) {
        this.enableAppointItemIds = enableAppointItemIds;
    }

    /**
     * 获取是否删除，是否有效；1-有效，0-无效
     *
     * @return inservice - 是否删除，是否有效；1-有效，0-无效
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否删除，是否有效；1-有效，0-无效
     *
     * @param inservice 是否删除，是否有效；1-有效，0-无效
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
     * 获取创建日期
     *
     * @return crt_time - 创建日期
     */
    public Date getCrtTime() {
        return crtTime;
    }

    /**
     * 设置创建日期
     *
     * @param crtTime 创建日期
     */
    public void setCrtTime(Date crtTime) {
        this.crtTime = crtTime;
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
     * 获取更新人名
     *
     * @return upd_name - 更新人名
     */
    public String getUpdName() {
        return updName;
    }

    /**
     * 设置更新人名
     *
     * @param updName 更新人名
     */
    public void setUpdName(String updName) {
        this.updName = updName;
    }

    /**
     * 获取更新日期
     *
     * @return upd_time - 更新日期
     */
    public Date getUpdTime() {
        return updTime;
    }

    /**
     * 设置更新日期
     *
     * @param updTime 更新日期
     */
    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }

    /**
     * 获取医生ID
     * @return 医生ID
     */
    public Integer getDentistId() {
        return dentistId;
    }

    /**
     * 设置医生ID
     * @param dentistId  医生ID
     */
    public void setDentistId(Integer dentistId) {
        this.dentistId = dentistId;
    }

    /**
     * 获取门诊ID
     * @return 门诊ID
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置门诊ID
     * @param orgId 门诊ID
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }
}