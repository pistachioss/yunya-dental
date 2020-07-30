package com.yunya.models.appointment;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

public class AppItem {
    /**
     * 主键ID
     */
    @Id
    private Integer id;

    @Column(name = "item_name")
    private String itemName;

    /**
     * 公司端对应的诊所ID
     */
    @Column(name = "comp_clin_id")
    private String compClinId;

    /**
     * 公司端对应预约的id
     */
    @Column(name = "comp_appitem_id")
    private String compAppitemId;

    /**
     * 预约项目编号
     */
    @Column(name = "item_no")
    private String itemNo;

    /**
     * 预约默认时长（分钟）
     */
    @Column(name = "app_duration")
    private String appDuration;

    /**
     * 创建人id
     */
    @Column(name = "crt_id")
    private String crtId;

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
     * 更新人id
     */
    @Column(name = "upd_id")
    private String updId;

    /**
     * 最后更新人
     */
    @Column(name = "upd_name")
    private String updName;

    /**
     * 最后更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 是否有效、是否启用、是否可见
     */
    private Boolean isvalid;

    /**
     * 是否删除，逻辑假删除
     */
    private Boolean isdeleted;

    /**
     * 获取主键ID
     *
     * @return id - 主键ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键ID
     *
     * @param id 主键ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取公司端对应的诊所ID
     *
     * @return comp_clin_id - 公司端对应的诊所ID
     */
    public String getCompClinId() {
        return compClinId;
    }

    /**
     * 设置公司端对应的诊所ID
     *
     * @param compClinId 公司端对应的诊所ID
     */
    public void setCompClinId(String compClinId) {
        this.compClinId = compClinId;
    }

    /**
     * 获取公司端对应表的ID
     *
     * @return comp_appitem_id - 公司端对应表的ID
     */
    public String getCompAppitemId() {
        return compAppitemId;
    }

    /**
     * 设置公司端对应表的ID
     *
     * @param compAppitemId 公司端对应表的ID
     */
    public void setCompAppitemId(String compAppitemId) {
        this.compAppitemId = compAppitemId;
    }

    /**
     * 获取预约项目编号
     *
     * @return item_no - 预约项目编号
     */
    public String getItemNo() {
        return itemNo;
    }

    /**
     * 设置预约项目编号
     *
     * @param itemNo 预约项目编号
     */
    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    /**
     * 获取预约默认时长（分钟）
     *
     * @return app_duration - 预约默认时长（分钟）
     */
    public String getAppDuration() {
        return appDuration;
    }

    /**
     * 设置预约默认时长（分钟）
     *
     * @param appDuration 预约默认时长（分钟）
     */
    public void setAppDuration(String appDuration) {
        this.appDuration = appDuration;
    }

    /**
     * 获取创建人id
     *
     * @return crt_id - 创建人id
     */
    public String getCrtId() {
        return crtId;
    }

    /**
     * 设置创建人id
     *
     * @param crtId 创建人id
     */
    public void setCrtId(String crtId) {
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
     * 获取更新人id
     *
     * @return upd_id - 更新人id
     */
    public String getUpdId() {
        return updId;
    }

    /**
     * 设置更新人id
     *
     * @param updId 更新人id
     */
    public void setUpdId(String updId) {
        this.updId = updId;
    }

    /**
     * 获取最后更新人
     *
     * @return upd_name - 最后更新人
     */
    public String getUpdName() {
        return updName;
    }

    /**
     * 设置最后更新人
     *
     * @param updName 最后更新人
     */
    public void setUpdName(String updName) {
        this.updName = updName;
    }

    /**
     * 获取最后更新时间
     *
     * @return upd_time - 最后更新时间
     */
    public Date getUpdTime() {
        return updTime;
    }

    /**
     * 设置最后更新时间
     *
     * @param updTime 最后更新时间
     */
    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }

    /**
     * 获取是否有效、是否启用、是否可见
     *
     * @return isvalid - 是否有效、是否启用、是否可见
     */
    public Boolean getIsvalid() {
        return isvalid;
    }

    /**
     * 设置是否有效、是否启用、是否可见
     *
     * @param isvalid 是否有效、是否启用、是否可见
     */
    public void setIsvalid(Boolean isvalid) {
        this.isvalid = isvalid;
    }

    /**
     * 获取是否删除，逻辑假删除
     *
     * @return isdeleted - 是否删除，逻辑假删除
     */
    public Boolean getIsdeleted() {
        return isdeleted;
    }

    /**
     * 设置是否删除，逻辑假删除
     *
     * @param isdeleted 是否删除，逻辑假删除
     */
    public void setIsdeleted(Boolean isdeleted) {
        this.isdeleted = isdeleted;
    }

    /**
     * 获取项目名称
     * @return
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * 设置项目名称
     * @param itemName
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}