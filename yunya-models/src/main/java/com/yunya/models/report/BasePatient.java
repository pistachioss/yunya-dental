package com.yunya.models.report;


import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "base_patient")
public class BasePatient {
    /**
     * 患者ID
     */
    @Id
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 诊所ID 添加患者的组织ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 患者姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 病例编号
     */
    @Column(name = "medical_number")
    private String medicalNumber;

    /**
     * 头像地址
     */
    @Column(name = "face_url")
    private String faceUrl;

    /**
     * 出生日期
     */
    private Date birthday;

    /**
     * 患者来源类型
     */
    @Column(name = "origin_type")
    private Integer originType;

    /**
     * 来源ID
     */
    @Column(name = "origin_id")
    private Integer originId;

    /**
     * 患者性别 0-男；1-女；2-未知
     */
    private Byte gender;

    /**
     * 初诊日期
     */
    @Column(name = "first_visit_date")
    private Date firstVisitDate;

    /**
     * 初诊门诊
     */
    @Column(name = "first_visit_outpatient")
    private String firstVisitOutpatient;

    /**
     * 初诊医生
     */
    @Column(name = "first_visit_doctors")
    private String firstVisitDoctors;

    /**
     * 末诊日期
     */
    @Column(name = "last_visit_date")
    private Date lastVisitDate;

    /**
     * 末诊门诊
     */
    @Column(name = "last_visit_outpatient")
    private String lastVisitOutpatient;

    /**
     * 末诊医生
     */
    @Column(name = "last_visit_doctors")
    private String lastVisitDoctors;

    /**
     * 累计消费
     */
    @Column(name = "cumulative_consumption")
    private Integer cumulativeConsumption;

    /**
     * 欠费总额
     */
    @Column(name = "total_arrears")
    private Integer totalArrears;

    /**
     * 就诊次数
     */
    @Column(name = "number_of_visits")
    private Integer numberOfVisits;

    /**
     * 患者名称拼音
     */
    @Column(name = "pinyin_name")
    private String pinyinName;

    /**
     * 末诊日期
     */
    @Column(name = "patient_crt_time")
    private Date patientCrtTime;

    /** 患者来源名称 */
    @Column(name = "origin_type_name")
    private String originTypeName;

    /**
     * 年平均就诊次数
     */
    @Column(name = "visit_rate")
    private String visitRate;

    /**
     * 会员得分
     */
    @Column(name = "vip_score")
    private String vipScore;

    /**
     * 本次统计的会员级别
     */
    @Column(name = "vip_logo")
    private String vipLogo;

    /**
     * 年平均就诊次数(上一次结果，旧)
     */
    @Column(name = "visit_rate_old")
    private String visitRateOld;

    /**
     * 会员得分(上一次结果，旧)
     */
    @Column(name = "vip_score_old")
    private String vipScoreOld;

    /**
     * 本次统计的会员级别(上一次结果，旧)
     */
    @Column(name = "vip_logo_old")
    private String vipLogoOld;

    /**
     * 获取患者来源名称
     * @return originTypeName
     */
    public String getOriginTypeName() {
        return originTypeName;
    }

    /**
     * 设置患者来源名称
     * @param originTypeName 患者来源名称
     */
    public void setOriginTypeName(String originTypeName) {
        this.originTypeName = originTypeName;
    }

    /**
     * 获取患者创建时间
     * @return
     */
    public Date getPatientCrtTime() {
        return patientCrtTime;
    }

    /**
     * 设置患者创建时间
     * @param patientCrtTime
     */
    public void setPatientCrtTime(Date patientCrtTime) {
        this.patientCrtTime = patientCrtTime;
    }

    /**
     * 获取患者拼音
     * @return String
     */
    public String getPinyinName() {
        return pinyinName;
    }

    /**
     * 设置患者拼音
     * @param pinyinName 患者拼音
     */
    public void setPinyinName(String pinyinName) {
        this.pinyinName = pinyinName;
    }

    /**
     * 获取患者ID
     *
     * @return patient_id - 患者ID
     */
    public Integer getPatientId() {
        return patientId;
    }

    /**
     * 设置患者ID
     *
     * @param patientId 患者ID
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * 获取诊所ID 添加患者的组织ID
     *
     * @return org_id - 诊所ID 添加患者的组织ID
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置诊所ID 添加患者的组织ID
     *
     * @param orgId 诊所ID 添加患者的组织ID
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取患者姓名
     *
     * @return name - 患者姓名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置患者姓名
     *
     * @param name 患者姓名
     */
    public void setName(String name) {
        this.name = name;
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
     * 获取病例编号
     *
     * @return medical_number - 病例编号
     */
    public String getMedicalNumber() {
        return medicalNumber;
    }

    /**
     * 设置病例编号
     *
     * @param medicalNumber 病例编号
     */
    public void setMedicalNumber(String medicalNumber) {
        this.medicalNumber = medicalNumber;
    }

    /**
     * 获取患者头像地址
     *
     * @return faceUrl
     */
    public String getFaceUrl() {
        return faceUrl;
    }

    /**
     * 设置患者头像地址
     *
     * @param faceUrl 头像地址
     */
    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

    /**
     * 获取出生日期
     *
     * @return birthday - 出生日期
     */
    public Date getBirthday() {
        return birthday;
    }

    /**
     * 设置出生日期
     *
     * @param birthday 出生日期
     */
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    /**
     * 获取患者来源类型
     *
     * @return origin_type - 患者来源类型
     */
    public Integer getOriginType() {
        return originType;
    }

    /**
     * 设置患者来源类型
     *
     * @param originType 患者来源类型
     */
    public void setOriginType(Integer originType) {
        this.originType = originType;
    }

    /**
     * 获取来源ID
     *
     * @return origin_id - 来源ID
     */
    public Integer getOriginId() {
        return originId;
    }

    /**
     * 设置来源ID
     *
     * @param originId 来源ID
     */
    public void setOriginId(Integer originId) {
        this.originId = originId;
    }

    /**
     * 获取患者性别 0-男；1-女；2-未知
     *
     * @return gender - 患者性别 0-男；1-女；2-未知
     */
    public Byte getGender() {
        return gender;
    }

    /**
     * 设置患者性别 0-男；1-女；2-未知
     *
     * @param gender 患者性别 0-男；1-女；2-未知
     */
    public void setGender(Byte gender) {
        this.gender = gender;
    }

    /**
     * 获取初诊日期
     *
     * @return first_visit_date - 初诊日期
     */
    public Date getFirstVisitDate() {
        return firstVisitDate;
    }

    /**
     * 设置初诊日期
     *
     * @param firstVisitDate 初诊日期
     */
    public void setFirstVisitDate(Date firstVisitDate) {
        this.firstVisitDate = firstVisitDate;
    }

    /**
     * 获取初诊门诊
     *
     * @return first_visit_outpatient - 初诊门诊
     */
    public String getFirstVisitOutpatient() {
        return firstVisitOutpatient;
    }

    /**
     * 设置初诊门诊
     *
     * @param firstVisitOutpatient 初诊门诊
     */
    public void setFirstVisitOutpatient(String firstVisitOutpatient) {
        this.firstVisitOutpatient = firstVisitOutpatient;
    }

    /**
     * 获取初诊医生
     *
     * @return first_visit_doctors - 初诊医生
     */
    public String getFirstVisitDoctors() {
        return firstVisitDoctors;
    }

    /**
     * 设置初诊医生
     *
     * @param firstVisitDoctors 初诊医生
     */
    public void setFirstVisitDoctors(String firstVisitDoctors) {
        this.firstVisitDoctors = firstVisitDoctors;
    }

    /**
     * 获取末诊日期
     *
     * @return last_visit_date - 末诊日期
     */
    public Date getLastVisitDate() {
        return lastVisitDate;
    }

    /**
     * 设置末诊日期
     *
     * @param lastVisitDate 末诊日期
     */
    public void setLastVisitDate(Date lastVisitDate) {
        this.lastVisitDate = lastVisitDate;
    }

    /**
     * 获取末诊门诊
     *
     * @return last_visit_outpatient - 末诊门诊
     */
    public String getLastVisitOutpatient() {
        return lastVisitOutpatient;
    }

    /**
     * 设置末诊门诊
     *
     * @param lastVisitOutpatient 末诊门诊
     */
    public void setLastVisitOutpatient(String lastVisitOutpatient) {
        this.lastVisitOutpatient = lastVisitOutpatient;
    }

    /**
     * 获取末诊医生
     *
     * @return last_visit_doctors - 末诊医生
     */
    public String getLastVisitDoctors() {
        return lastVisitDoctors;
    }

    /**
     * 设置末诊医生
     *
     * @param lastVisitDoctors 末诊医生
     */
    public void setLastVisitDoctors(String lastVisitDoctors) {
        this.lastVisitDoctors = lastVisitDoctors;
    }

    /**
     * 获取累计消费
     *
     * @return cumulative_consumption - 累计消费
     */
    public Integer getCumulativeConsumption() {
        return cumulativeConsumption;
    }

    /**
     * 设置累计消费
     *
     * @param cumulativeConsumption 累计消费
     */
    public void setCumulativeConsumption(Integer cumulativeConsumption) {
        this.cumulativeConsumption = cumulativeConsumption;
    }

    /**
     * 获取欠费总额
     *
     * @return total_arrears - 欠费总额
     */
    public Integer getTotalArrears() {
        return totalArrears;
    }

    /**
     * 设置欠费总额
     *
     * @param totalArrears 欠费总额
     */
    public void setTotalArrears(Integer totalArrears) {
        this.totalArrears = totalArrears;
    }

    public Integer getNumberOfVisits() {
        return numberOfVisits;
    }

    public void setNumberOfVisits(Integer numberOfVisits) {
        this.numberOfVisits = numberOfVisits;
    }

    public String getVisitRate() {
        return visitRate;
    }

    public void setVisitRate(String visitRate) {
        this.visitRate = visitRate;
    }

    public String getVipScore() {
        return vipScore;
    }

    public void setVipScore(String vipScore) {
        this.vipScore = vipScore;
    }

    public String getVipLogo() {
        return vipLogo;
    }

    public void setVipLogo(String vipLogo) {
        this.vipLogo = vipLogo;
    }

    public String getVisitRateOld() {
        return visitRateOld;
    }

    public void setVisitRateOld(String visitRateOld) {
        this.visitRateOld = visitRateOld;
    }

    public String getVipScoreOld() {
        return vipScoreOld;
    }

    public void setVipScoreOld(String vipScoreOld) {
        this.vipScoreOld = vipScoreOld;
    }

    public String getVipLogoOld() {
        return vipLogoOld;
    }

    public void setVipLogoOld(String vipLogoOld) {
        this.vipLogoOld = vipLogoOld;
    }
}