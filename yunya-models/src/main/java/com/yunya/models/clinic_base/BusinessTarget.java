package com.yunya.models.clinic_base;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "business_target")
public class BusinessTarget {
    /**
     * 业务目标ID
     */
    @Id
    private Integer id;

    /**
     * 所属类型（0-组织，1-个人）
     */
    @Column(name = "belong_type")
    private Byte belongType;

    /**
     * 数据所属ID
     */
    @Column(name = "belong_id")
    private Integer belongId;

    /**
     * 业务目标类型（0-实收金额；1-工作量；2-初诊人数；3-就诊人次）
     */
    @Column(name = "business_type")
    private Byte businessType;

    /**
     * 业务目标数
     */
    @Column(name = "business_goal")
    private BigDecimal businessGoal;

    /**
     * 业务目标所属年份
     */
    @Column(name = "business_year")
    private String businessYear;

    /**
     * 业务目标所属月份
     */
    @Column(name = "business_month")
    private String businessMonth;

    /**
     * 业务目标所属年月
     */
    @Column(name = "business_date")
    private String businessDate;

    /**
     * 单位
     */
    private String unit;

    /**
     * 是否有效/是否启用
     */
    private Boolean inservice;

    /**
     * 创建人ID
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建人姓名
     */
    @Column(name = "crt_name")
    private String crtName;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新时间
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 更新人姓名
     */
    @Column(name = "upd_name")
    private String updName;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 获取业务目标ID
     *
     * @return id - 业务目标ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置业务目标ID
     *
     * @param id 业务目标ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取所属类型（0-组织，1-个人）
     *
     * @return belong_type - 所属类型（0-组织，1-个人）
     */
    public Byte getBelongType() {
        return belongType;
    }

    /**
     * 设置所属类型（0-组织，1-个人）
     *
     * @param belongType 所属类型（0-组织，1-个人）
     */
    public void setBelongType(Byte belongType) {
        this.belongType = belongType;
    }

    /**
     * 获取数据所属ID
     *
     * @return belong_id - 数据所属ID
     */
    public Integer getBelongId() {
        return belongId;
    }

    /**
     * 设置数据所属ID
     *
     * @param belongId 数据所属ID
     */
    public void setBelongId(Integer belongId) {
        this.belongId = belongId;
    }

    /**
     * 获取业务目标类型（0-实收金额；1-工作量；2-初诊人数；3-就诊人次）
     *
     * @return business_type - 业务目标类型（0-实收金额；1-工作量；2-初诊人数；3-就诊人次）
     */
    public Byte getBusinessType() {
        return businessType;
    }

    /**
     * 设置业务目标类型（0-实收金额；1-工作量；2-初诊人数；3-就诊人次）
     *
     * @param businessType 业务目标类型（0-实收金额；1-工作量；2-初诊人数；3-就诊人次）
     */
    public void setBusinessType(Byte businessType) {
        this.businessType = businessType;
    }

    /**
     * 获取业务目标数
     *
     * @return business_goal - 业务目标数
     */
    public BigDecimal getBusinessGoal() {
        return businessGoal;
    }

    /**
     * 设置业务目标数
     *
     * @param businessGoal 业务目标数
     */
    public void setBusinessGoal(BigDecimal businessGoal) {
        this.businessGoal = businessGoal;
    }

    /**
     * 获取业务目标时间
     *
     * @return business_year - 业务目标时间年
     */
    public String getBusinessYear() {
        return businessYear;
    }

    /**
     * 设置业务目标时间
     *
     * @param businessYear 业务目标时间年
     */
    public void setBusinessYear(String businessYear) {
    this.businessYear = businessYear;
    }

    /**
     * 获取业务目标时间月
     *
     * @return business_date - 业务目标时间月
     */
    public String getBusinessMonth() {
        return businessMonth;
    }

    /**
     * 设置业务目标时间年月
     *
     * @return business_date - 业务目标时间
     */
    public void setBusinessMonth(String businessMonth) {
        this.businessMonth = businessMonth;
    }

    /**
     * 获取业务目标时间月
     *
     * @return business_date - 业务目标时间月
     */
    public String getBusinessDate() {
        return businessDate;
    }

    /**
     * 设置业务目标时间年月
     *
     * @return business_date - 业务目标时间
     */
    public void setBusinessDate(String businessDate) {
        this.businessDate = businessDate;
    }

    /**
     * 获取单位
     *
     * @return unit - 单位
     */
    public String getUnit() {
        return unit;
    }

    /**
     * 设置单位
     *
     * @param unit 单位
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * 获取是否有效/是否启用
     *
     * @return inservice - 是否有效/是否启用
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否有效/是否启用
     *
     * @param inservice 是否有效/是否启用
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
     * 获取创建人姓名
     *
     * @return crt_name - 创建人姓名
     */
    public String getCrtName() {
        return crtName;
    }

    /**
     * 设置创建人姓名
     *
     * @param crtName 创建人姓名
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
     * 获取更新时间
     *
     * @return upd_id - 更新时间
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * 设置更新时间
     *
     * @param updId 更新时间
     */
    public void setUpdId(Integer updId) {
        this.updId = updId;
    }

    /**
     * 获取更新人姓名
     *
     * @return upd_name - 更新人姓名
     */
    public String getUpdName() {
        return updName;
    }

    /**
     * 设置更新人姓名
     *
     * @param updName 更新人姓名
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
}