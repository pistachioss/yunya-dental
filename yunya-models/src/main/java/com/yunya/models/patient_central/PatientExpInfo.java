package com.yunya.models.patient_central;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "patient_exp_info")
public class PatientExpInfo {
    /**
     * 主键
     */
    @Id
    private Integer id;

    /**
     * 患者ID 患者ID
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 患者类型 患者类型对应字典ID
     */
    @Column(name = "patient_kind")
    private Integer patientKind;

    /**
     * 常用电话 常用电话
     */
    @Column(name = "useful_phone")
    private String usefulPhone;

    /**
     * 身份证号
     */
    private String identity;

    /**
     * 职业字典明细ID 职业对应字典ID
     */
    private Integer profession;

    /**
     * 遗传病史
     */
    private String heredity;

    /**
     * 其他健康情况
     */
    @Column(name = "other_health")
    private String otherHealth;

    /**
     * 每日刷牙次数
     */
    @Column(name = "brush_times")
    private Integer brushTimes;

    /**
     * 每次刷牙时长
     */
    @Column(name = "brush_time")
    private Byte brushTime;

    /**
     * 刷毛硬度 0-软；1-中；2-硬
     */
    @Column(name = "brush_hardness")
    private Byte brushHardness;

    /**
     * 烟龄
     */
    @Column(name = "smoking_age")
    private Byte smokingAge;

    /**
     * 每日吸烟数量
     */
    @Column(name = "smoking_num")
    private Integer smokingNum;

    /**
     * 是否使用电动牙刷
     */
    @Column(name = "use_electric_brush")
    private Boolean useElectricBrush;

    /**
     * 是否使用漱口水
     */
    @Column(name = "use_collutory")
    private Boolean useCollutory;

    /**
     * 是否适用牙线
     */
    @Column(name = "use_floss")
    private Boolean useFloss;

    /**
     * 是否夜磨牙
     */
    private Boolean bruxism;

    /**
     * 孕龄时长
     */
    private Integer pregnancyWeek;

    /**
     * 是否哺乳
     */
    @Column(name = "is_feed_baby")
    private Boolean feedBaby;

    /**
     * 监护人
     */
    private String guardian;

    /**
     * 家庭详细地址
     */
    private String address;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String country;

    /**
     * 备注 备注
     */
    private String remarks;

    /**
     * 是否启用 是否有效
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
     * 更新人ID
     */
    @Column(name = "upt_id")
    private Integer uptId;

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
     * 获取主键
     *
     * @return id - 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取患者ID 患者ID
     *
     * @return patient_id - 患者ID 患者ID
     */
    public Integer getPatientId() {
        return patientId;
    }

    /**
     * 设置患者ID 患者ID
     *
     * @param patientId 患者ID 患者ID
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * 获取患者类型 患者类型对应字典ID
     *
     * @return patient_kind - 患者类型 患者类型对应字典ID
     */
    public Integer getPatientKind() {
        return patientKind;
    }

    /**
     * 设置患者类型 患者类型对应字典ID
     *
     * @param patientKind 患者类型 患者类型对应字典ID
     */
    public void setPatientKind(Integer patientKind) {
        this.patientKind = patientKind;
    }

    /**
     * 获取常用电话 常用电话
     *
     * @return useful_phone - 常用电话 常用电话
     */
    public String getUsefulPhone() {
        return usefulPhone;
    }

    /**
     * 设置常用电话 常用电话
     *
     * @param usefulPhone 常用电话 常用电话
     */
    public void setUsefulPhone(String usefulPhone) {
        this.usefulPhone = usefulPhone;
    }

    /**
     * 获取身份证号
     *
     * @return identity - 身份证号
     */
    public String getIdentity() {
        return identity;
    }

    /**
     * 设置身份证号
     *
     * @param identity 身份证号
     */
    public void setIdentity(String identity) {
        this.identity = identity;
    }

    /**
     * 获取职业字典明细ID 职业对应字典ID
     *
     * @return profession - 职业字典明细ID 职业对应字典ID
     */
    public Integer getProfession() {
        return profession;
    }

    /**
     * 设置职业字典明细ID 职业对应字典ID
     *
     * @param profession 职业字典明细ID 职业对应字典ID
     */
    public void setProfession(Integer profession) {
        this.profession = profession;
    }

    /**
     * 获取遗传病史
     *
     * @return heredity - 遗传病史
     */
    public String getHeredity() {
        return heredity;
    }

    /**
     * 设置遗传病史
     *
     * @param heredity 遗传病史
     */
    public void setHeredity(String heredity) {
        this.heredity = heredity;
    }

    /**
     * 获取其他健康情况
     *
     * @return other_health - 其他健康情况
     */
    public String getOtherHealth() {
        return otherHealth;
    }

    /**
     * 设置其他健康情况
     *
     * @param otherHealth 其他健康情况
     */
    public void setOtherHealth(String otherHealth) {
        this.otherHealth = otherHealth;
    }

    /**
     * 获取每日刷牙次数
     *
     * @return brush_times - 每日刷牙次数
     */
    public Integer getBrushTimes() {
        return brushTimes;
    }

    /**
     * 设置每日刷牙次数
     *
     * @param brushTimes 每日刷牙次数
     */
    public void setBrushTimes(Integer brushTimes) {
        this.brushTimes = brushTimes;
    }

    /**
     * 获取每次刷牙时长
     *
     * @return brush_time - 每次刷牙时长
     */
    public Byte getBrushTime() {
        return brushTime;
    }

    /**
     * 设置每次刷牙时长
     *
     * @param brushTime 每次刷牙时长
     */
    public void setBrushTime(Byte brushTime) {
        this.brushTime = brushTime;
    }

    /**
     * 获取刷毛硬度 0-软；1-中；2-硬
     *
     * @return brush_hardness - 刷毛硬度 0-软；1-中；2-硬
     */
    public Byte getBrushHardness() {
        return brushHardness;
    }

    /**
     * 设置刷毛硬度 0-软；1-中；2-硬
     *
     * @param brushHardness 刷毛硬度 0-软；1-中；2-硬
     */
    public void setBrushHardness(Byte brushHardness) {
        this.brushHardness = brushHardness;
    }

    /**
     * 获取烟龄
     *
     * @return smoking_age - 烟龄
     */
    public Byte getSmokingAge() {
        return smokingAge;
    }

    /**
     * 设置烟龄
     *
     * @param smokingAge 烟龄
     */
    public void setSmokingAge(Byte smokingAge) {
        this.smokingAge = smokingAge;
    }

    /**
     * 获取每日吸烟数量
     *
     * @return smoking_num - 每日吸烟数量
     */
    public Integer getSmokingNum() {
        return smokingNum;
    }

    /**
     * 设置每日吸烟数量
     *
     * @param smokingNum 每日吸烟数量
     */
    public void setSmokingNum(Integer smokingNum) {
        this.smokingNum = smokingNum;
    }

    /**
     * 获取是否使用电动牙刷
     *
     * @return use_electric_brush - 是否使用电动牙刷
     */
    public Boolean getUseElectricBrush() {
        return useElectricBrush;
    }

    /**
     * 设置是否使用电动牙刷
     *
     * @param useElectricBrush 是否使用电动牙刷
     */
    public void setUseElectricBrush(Boolean useElectricBrush) {
        this.useElectricBrush = useElectricBrush;
    }

    /**
     * 获取是否使用漱口水
     *
     * @return use_collutory - 是否使用漱口水
     */
    public Boolean getUseCollutory() {
        return useCollutory;
    }

    /**
     * 设置是否使用漱口水
     *
     * @param useCollutory 是否使用漱口水
     */
    public void setUseCollutory(Boolean useCollutory) {
        this.useCollutory = useCollutory;
    }

    /**
     * 获取是否适用牙线
     *
     * @return use_floss - 是否适用牙线
     */
    public Boolean getUseFloss() {
        return useFloss;
    }

    /**
     * 设置是否适用牙线
     *
     * @param useFloss 是否适用牙线
     */
    public void setUseFloss(Boolean useFloss) {
        this.useFloss = useFloss;
    }

    /**
     * 获取是否夜磨牙
     *
     * @return bruxism - 是否夜磨牙
     */
    public Boolean getBruxism() {
        return bruxism;
    }

    /**
     * 设置是否夜磨牙
     *
     * @param bruxism 是否夜磨牙
     */
    public void setBruxism(Boolean bruxism) {
        this.bruxism = bruxism;
    }

    /**
     * 获取家庭详细地址
     *
     * @return address - 家庭详细地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 设置家庭详细地址
     *
     * @param address 家庭详细地址
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 获取省
     *
     * @return province - 省
     */
    public String getProvince() {
        return province;
    }

    /**
     * 设置省
     *
     * @param province 省
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * 获取市
     *
     * @return city - 市
     */
    public String getCity() {
        return city;
    }

    /**
     * 设置市
     *
     * @param city 市
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * 获取区
     *
     * @return country - 区
     */
    public String getCountry() {
        return country;
    }

    /**
     * 设置区
     *
     * @param country 区
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * 获取备注 备注
     *
     * @return remarks - 备注 备注
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * 设置备注 备注
     *
     * @param remarks 备注 备注
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * 获取是否启用 是否有效
     *
     * @return inservice - 是否启用 是否有效
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否启用 是否有效
     *
     * @param inservice 是否启用 是否有效
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
     * 获取更新人ID
     *
     * @return upt_id - 更新人ID
     */
    public Integer getUptId() {
        return uptId;
    }

    /**
     * 设置更新人ID
     *
     * @param uptId 更新人ID
     */
    public void setUptId(Integer uptId) {
        this.uptId = uptId;
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

    public Integer getPregnancyWeek() {
        return pregnancyWeek;
    }

    public void setPregnancyWeek(Integer pregnancyWeek) {
        this.pregnancyWeek = pregnancyWeek;
    }

    public Boolean getFeedBaby() {
        return feedBaby;
    }

    public void setFeedBaby(Boolean feedBaby) {
        this.feedBaby = feedBaby;
    }

    public String getGuardian() {
        return guardian;
    }

    public void setGuardian(String guardian) {
        this.guardian = guardian;
    }
}