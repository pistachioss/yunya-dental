package com.yunya.models.patient_central;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "patient_child_info")
public class PatientChildInfo {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 患者id
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 学校
     */
    private String school;

    /**
     * 年级
     */
    private String grade;

    /**
     * 服药史
     */
    @Column(name = "medication_history")
    private String medicationHistory;

    /**
     * 饮食结构
     */
    private String diet;

    /**
     * 牙齿萌生情况
     */
    @Column(name = "tooth_sprouting")
    private String toothSprouting;

    /**
     * 牙齿清洁情况
     */
    @Column(name = "tooth_clearliness")
    private String toothClearliness;

    /**
     * 每天刷牙次数
     */
    @Column(name = "brushing_times")
    private Short brushingTimes;

    /**
     * 是否使用含氟牙膏
     */
    @Column(name = "used_fluoride_toothpaste")
    private Boolean usedFluorideToothpaste;

    /**
     * 使用牙线情况：0-有，1-无，2-偶尔
     */
    @Column(name = "`used_dental_ floss`")
    private Byte usedDentalFloss;

    /**
     * 每周使用牙线次数
     */
    @Column(name = "use_floss_times")
    private Integer useFlossTimes;

    /**
     * 父母是否有龋齿：0-父有，1-父无，2-母有，3-母无；逗号分隔
     */
    @Column(name = "parent_has_caries")
    private String parentHasCaries;

    /**
     * 母亲孕期情况
     */
    @Column(name = "mother_pregnancy")
    private String motherPregnancy;

    /**
     * 习惯（字典id列表），多个逗号分隔
     */
    @Column(name = "habit_ids")
    private String habitIds;

    /**
     * 最近一次检查牙齿日期
     */
    @Column(name = "tooth_last_check")
    private Date toothLastCheck;

    /**
     * 是否启用
     */
    private Boolean inservice;

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
     * 更新人id
     */
    @Column(name = "upt_id")
    private Integer uptId;

    /**
     * 更新时间
     */
    @Column(name = "upt_time")
    private Date uptTime;

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
     * 获取患者id
     *
     * @return patient_id - 患者id
     */
    public Integer getPatientId() {
        return patientId;
    }

    /**
     * 设置患者id
     *
     * @param patientId 患者id
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * 获取学校
     *
     * @return school - 学校
     */
    public String getSchool() {
        return school;
    }

    /**
     * 设置学校
     *
     * @param school 学校
     */
    public void setSchool(String school) {
        this.school = school;
    }

    /**
     * 获取年级
     *
     * @return grade - 年级
     */
    public String getGrade() {
        return grade;
    }

    /**
     * 设置年级
     *
     * @param grade 年级
     */
    public void setGrade(String grade) {
        this.grade = grade;
    }

    /**
     * 获取服药史
     *
     * @return medication_history - 服药史
     */
    public String getMedicationHistory() {
        return medicationHistory;
    }

    /**
     * 设置服药史
     *
     * @param medicationHistory 服药史
     */
    public void setMedicationHistory(String medicationHistory) {
        this.medicationHistory = medicationHistory;
    }

    /**
     * 获取饮食结构
     *
     * @return diet - 饮食结构
     */
    public String getDiet() {
        return diet;
    }

    /**
     * 设置饮食结构
     *
     * @param diet 饮食结构
     */
    public void setDiet(String diet) {
        this.diet = diet;
    }

    /**
     * 获取牙齿萌生情况
     *
     * @return tooth_sprouting - 牙齿萌生情况
     */
    public String getToothSprouting() {
        return toothSprouting;
    }

    /**
     * 设置牙齿萌生情况
     *
     * @param toothSprouting 牙齿萌生情况
     */
    public void setToothSprouting(String toothSprouting) {
        this.toothSprouting = toothSprouting;
    }

    /**
     * 获取牙齿清洁情况
     *
     * @return tooth_clearliness - 牙齿清洁情况
     */
    public String getToothClearliness() {
        return toothClearliness;
    }

    /**
     * 设置牙齿清洁情况
     *
     * @param toothClearliness 牙齿清洁情况
     */
    public void setToothClearliness(String toothClearliness) {
        this.toothClearliness = toothClearliness;
    }

    /**
     * 获取每天刷牙次数
     *
     * @return brushing_times - 每天刷牙次数
     */
    public Short getBrushingTimes() {
        return brushingTimes;
    }

    /**
     * 设置每天刷牙次数
     *
     * @param brushingTimes 每天刷牙次数
     */
    public void setBrushingTimes(Short brushingTimes) {
        this.brushingTimes = brushingTimes;
    }

    /**
     * 获取是否使用含氟牙膏
     *
     * @return used_fluoride_toothpaste - 是否使用含氟牙膏
     */
    public Boolean getUsedFluorideToothpaste() {
        return usedFluorideToothpaste;
    }

    /**
     * 设置是否使用含氟牙膏
     *
     * @param usedFluorideToothpaste 是否使用含氟牙膏
     */
    public void setUsedFluorideToothpaste(Boolean usedFluorideToothpaste) {
        this.usedFluorideToothpaste = usedFluorideToothpaste;
    }

    /**
     * 获取使用牙线情况：0-有，1-无，2-偶尔
     *
     * @return used_dental_ floss - 使用牙线情况：0-有，1-无，2-偶尔
     */
    public Byte getUsedDentalFloss() {
        return usedDentalFloss;
    }

    /**
     * 设置使用牙线情况：0-有，1-无，2-偶尔
     *
     * @param usedDentalFloss 使用牙线情况：0-有，1-无，2-偶尔
     */
    public void setUsedDentalFloss(Byte usedDentalFloss) {
        this.usedDentalFloss = usedDentalFloss;
    }

    /**
     * 获取每周使用牙线次数
     *
     * @return use_floss_times - 每周使用牙线次数
     */
    public Integer getUseFlossTimes() {
        return useFlossTimes;
    }

    /**
     * 设置每周使用牙线次数
     *
     * @param useFlossTimes 每周使用牙线次数
     */
    public void setUseFlossTimes(Integer useFlossTimes) {
        this.useFlossTimes = useFlossTimes;
    }

    /**
     * 获取父母是否有龋齿：0-父有，1-父无，2-母有，3-母无；逗号分隔
     *
     * @return parent_has_caries - 父母是否有龋齿：0-父有，1-父无，2-母有，3-母无；逗号分隔
     */
    public String getParentHasCaries() {
        return parentHasCaries;
    }

    /**
     * 设置父母是否有龋齿：0-父有，1-父无，2-母有，3-母无；逗号分隔
     *
     * @param parentHasCaries 父母是否有龋齿：0-父有，1-父无，2-母有，3-母无；逗号分隔
     */
    public void setParentHasCaries(String parentHasCaries) {
        this.parentHasCaries = parentHasCaries;
    }

    /**
     * 获取母亲孕期情况
     *
     * @return mother_pregnancy - 母亲孕期情况
     */
    public String getMotherPregnancy() {
        return motherPregnancy;
    }

    /**
     * 设置母亲孕期情况
     *
     * @param motherPregnancy 母亲孕期情况
     */
    public void setMotherPregnancy(String motherPregnancy) {
        this.motherPregnancy = motherPregnancy;
    }

    /**
     * 获取习惯（字典id列表），多个逗号分隔
     *
     * @return habit_ids - 习惯（字典id列表），多个逗号分隔
     */
    public String getHabitIds() {
        return habitIds;
    }

    /**
     * 设置习惯（字典id列表），多个逗号分隔
     *
     * @param habitIds 习惯（字典id列表），多个逗号分隔
     */
    public void setHabitIds(String habitIds) {
        this.habitIds = habitIds;
    }

    /**
     * 获取最近一次检查牙齿日期
     *
     * @return tooth_last_check - 最近一次检查牙齿日期
     */
    public Date getToothLastCheck() {
        return toothLastCheck;
    }

    /**
     * 设置最近一次检查牙齿日期
     *
     * @param toothLastCheck 最近一次检查牙齿日期
     */
    public void setToothLastCheck(Date toothLastCheck) {
        this.toothLastCheck = toothLastCheck;
    }

    /**
     * 获取是否启用
     *
     * @return inservice - 是否启用
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否启用
     *
     * @param inservice 是否启用
     */
    public void setInservice(Boolean inservice) {
        this.inservice = inservice;
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
     * 获取更新人id
     *
     * @return upt_id - 更新人id
     */
    public Integer getUptId() {
        return uptId;
    }

    /**
     * 设置更新人id
     *
     * @param uptId 更新人id
     */
    public void setUptId(Integer uptId) {
        this.uptId = uptId;
    }

    /**
     * 获取更新时间
     *
     * @return upt_time - 更新时间
     */
    public Date getUptTime() {
        return uptTime;
    }

    /**
     * 设置更新时间
     *
     * @param uptTime 更新时间
     */
    public void setUptTime(Date uptTime) {
        this.uptTime = uptTime;
    }
}