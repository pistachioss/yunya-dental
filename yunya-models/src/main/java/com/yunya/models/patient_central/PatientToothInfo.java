package com.yunya.models.patient_central;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "patient_tooth_info")
public class PatientToothInfo {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 患者id
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 是否有缺牙史
     */
    @Column(name = "had_miss_tooth")
    private Boolean hadMissTooth;

    /**
     * 缺牙史-缺牙位置列表，多个逗号分隔
     */
    @Column(name = "miss_tooth_history")
    private String missToothHistory;

    /**
     * 是否有填充治疗史
     */
    @Column(name = "had_fill_treat")
    private Boolean hadFillTreat;

    /**
     * 填充治疗史-字典材料id，多个逗号分隔
     */
    @Column(name = "fill_treat_history")
    private String fillTreatHistory;

    /**
     * 最近一次填充治疗日期
     */
    @Column(name = "fill_treat_last_date")
    private Date fillTreatLastDate;

    /**
     * 是否牙周手术
     */
    @Column(name = "had_periodontal_surgery")
    private Boolean hadPeriodontalSurgery;

    /**
     * 是否咬合调整
     */
    @Column(name = "had_occlusal_adjust")
    private Boolean hadOcclusalAdjust;

    /**
     * 是否修复义齿
     */
    @Column(name = "had_restorative_dentures")
    private Boolean hadRestorativeDentures;

    /**
     * RPD部位
     */
    @Column(name = "rpd_part")
    private String rpdPart;

    /**
     * RPD佩戴日期
     */
    @Column(name = "rpd_date")
    private Date rpdDate;

    /**
     * LPD部位
     */
    @Column(name = "lpd_part")
    private String lpdPart;

    /**
     * LPD佩戴日期
     */
    @Column(name = "lpd_date")
    private Date lpdDate;

    /**
     * 是否预防治疗
     */
    @Column(name = "had_preventive_treat")
    private Boolean hadPreventiveTreat;

    /**
     * 预防治疗周期
     */
    @Column(name = "preventive_treat_cycle")
    private Short preventiveTreatCycle;

    /**
     * 上次预防治疗距今
     */
    @Column(name = "preventive_treat_last_month")
    private Short preventiveTreatLastMonth;

    /**
     * 是否有使用困难或不舒适的经历
     */
    @Column(name = "had_diffcult_treat")
    private Boolean hadDiffcultTreat;

    /**
     * 缺牙但不修复的原因
     */
    @Column(name = "miss_teeth_unrepeat_cause")
    private String missTeethUnrepeatCause;

    /**
     * 是否正畸治疗
     */
    @Column(name = "had_orthodontic")
    private Boolean hadOrthodontic;

    /**
     * 正畸治疗开始日期
     */
    @Column(name = "orthodontic_start_date")
    private Date orthodonticStartDate;

    /**
     * 正畸治疗结束日期
     */
    @Column(name = "orthodontic_end_date")
    private Date orthodonticEndDate;

    /**
     * 是否接受过口腔卫生宣教
     */
    @Column(name = "had_hygiene_education")
    private Boolean hadHygieneEducation;

    /**
     * 是否用过菌斑染色体
     */
    @Column(name = "used_plaque_dna")
    private Boolean usedPlaqueDna;

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
     * 创建日期
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人ID
     */
    @Column(name = "upt_id")
    private Integer uptId;

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

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * 获取是否有缺牙史
     *
     * @return had_miss_tooth - 是否有缺牙史
     */
    public Boolean getHadMissTooth() {
        return hadMissTooth;
    }

    /**
     * 设置是否有缺牙史
     *
     * @param hadMissTooth 是否有缺牙史
     */
    public void setHadMissTooth(Boolean hadMissTooth) {
        this.hadMissTooth = hadMissTooth;
    }

    /**
     * 获取缺牙史-缺牙位置列表，多个逗号分隔
     *
     * @return miss_tooth_history - 缺牙史-缺牙位置列表，多个逗号分隔
     */
    public String getMissToothHistory() {
        return missToothHistory;
    }

    /**
     * 设置缺牙史-缺牙位置列表，多个逗号分隔
     *
     * @param missToothHistory 缺牙史-缺牙位置列表，多个逗号分隔
     */
    public void setMissToothHistory(String missToothHistory) {
        this.missToothHistory = missToothHistory;
    }

    /**
     * 获取是否有填充治疗史
     *
     * @return had_fill_treat - 是否有填充治疗史
     */
    public Boolean getHadFillTreat() {
        return hadFillTreat;
    }

    /**
     * 设置是否有填充治疗史
     *
     * @param hadFillTreat 是否有填充治疗史
     */
    public void setHadFillTreat(Boolean hadFillTreat) {
        this.hadFillTreat = hadFillTreat;
    }

    /**
     * 获取填充治疗史-字典材料id，多个逗号分隔
     *
     * @return fill_treat_history - 填充治疗史-字典材料id，多个逗号分隔
     */
    public String getFillTreatHistory() {
        return fillTreatHistory;
    }

    /**
     * 设置填充治疗史-字典材料id，多个逗号分隔
     *
     * @param fillTreatHistory 填充治疗史-字典材料id，多个逗号分隔
     */
    public void setFillTreatHistory(String fillTreatHistory) {
        this.fillTreatHistory = fillTreatHistory;
    }

    /**
     * 获取最近一次填充治疗日期
     *
     * @return fill_treat_last_date - 最近一次填充治疗日期
     */
    public Date getFillTreatLastDate() {
        return fillTreatLastDate;
    }

    /**
     * 设置最近一次填充治疗日期
     *
     * @param fillTreatLastDate 最近一次填充治疗日期
     */
    public void setFillTreatLastDate(Date fillTreatLastDate) {
        this.fillTreatLastDate = fillTreatLastDate;
    }

    /**
     * 获取是否牙周手术
     *
     * @return had_periodontal_surgery - 是否牙周手术
     */
    public Boolean getHadPeriodontalSurgery() {
        return hadPeriodontalSurgery;
    }

    /**
     * 设置是否牙周手术
     *
     * @param hadPeriodontalSurgery 是否牙周手术
     */
    public void setHadPeriodontalSurgery(Boolean hadPeriodontalSurgery) {
        this.hadPeriodontalSurgery = hadPeriodontalSurgery;
    }

    /**
     * 获取是否咬合调整
     *
     * @return had_occlusal_adjust - 是否咬合调整
     */
    public Boolean getHadOcclusalAdjust() {
        return hadOcclusalAdjust;
    }

    /**
     * 设置是否咬合调整
     *
     * @param hadOcclusalAdjust 是否咬合调整
     */
    public void setHadOcclusalAdjust(Boolean hadOcclusalAdjust) {
        this.hadOcclusalAdjust = hadOcclusalAdjust;
    }

    /**
     * 获取是否修复义齿
     *
     * @return had_restorative_dentures - 是否修复义齿
     */
    public Boolean getHadRestorativeDentures() {
        return hadRestorativeDentures;
    }

    /**
     * 设置是否修复义齿
     *
     * @param hadRestorativeDentures 是否修复义齿
     */
    public void setHadRestorativeDentures(Boolean hadRestorativeDentures) {
        this.hadRestorativeDentures = hadRestorativeDentures;
    }

    /**
     * 获取RPD部位
     *
     * @return rpd_part - RPD部位
     */
    public String getRpdPart() {
        return rpdPart;
    }

    /**
     * 设置RPD部位
     *
     * @param rpdPart RPD部位
     */
    public void setRpdPart(String rpdPart) {
        this.rpdPart = rpdPart;
    }

    /**
     * 获取RPD佩戴日期
     *
     * @return rpd_date - RPD佩戴日期
     */
    public Date getRpdDate() {
        return rpdDate;
    }

    /**
     * 设置RPD佩戴日期
     *
     * @param rpdDate RPD佩戴日期
     */
    public void setRpdDate(Date rpdDate) {
        this.rpdDate = rpdDate;
    }

    /**
     * 获取LPD部位
     *
     * @return lpd_part - LPD部位
     */
    public String getLpdPart() {
        return lpdPart;
    }

    /**
     * 设置LPD部位
     *
     * @param lpdPart LPD部位
     */
    public void setLpdPart(String lpdPart) {
        this.lpdPart = lpdPart;
    }

    /**
     * 获取LPD佩戴日期
     *
     * @return lpd_date - LPD佩戴日期
     */
    public Date getLpdDate() {
        return lpdDate;
    }

    /**
     * 设置LPD佩戴日期
     *
     * @param lpdDate LPD佩戴日期
     */
    public void setLpdDate(Date lpdDate) {
        this.lpdDate = lpdDate;
    }

    /**
     * 获取是否预防治疗
     *
     * @return had_preventive_treat - 是否预防治疗
     */
    public Boolean getHadPreventiveTreat() {
        return hadPreventiveTreat;
    }

    /**
     * 设置是否预防治疗
     *
     * @param hadPreventiveTreat 是否预防治疗
     */
    public void setHadPreventiveTreat(Boolean hadPreventiveTreat) {
        this.hadPreventiveTreat = hadPreventiveTreat;
    }

    /**
     * 获取预防治疗周期
     *
     * @return preventive_treat_cycle - 预防治疗周期
     */
    public Short getPreventiveTreatCycle() {
        return preventiveTreatCycle;
    }

    /**
     * 设置预防治疗周期
     *
     * @param preventiveTreatCycle 预防治疗周期
     */
    public void setPreventiveTreatCycle(Short preventiveTreatCycle) {
        this.preventiveTreatCycle = preventiveTreatCycle;
    }

    /**
     * 获取上次预防治疗距今
     *
     * @return preventive_treat_last_month - 上次预防治疗距今
     */
    public Short getPreventiveTreatLastMonth() {
        return preventiveTreatLastMonth;
    }

    /**
     * 设置上次预防治疗距今
     *
     * @param preventiveTreatLastMonth 上次预防治疗距今
     */
    public void setPreventiveTreatLastMonth(Short preventiveTreatLastMonth) {
        this.preventiveTreatLastMonth = preventiveTreatLastMonth;
    }

    /**
     * 获取是否有使用困难或不舒适的经历
     *
     * @return had_diffcult_treat - 是否有使用困难或不舒适的经历
     */
    public Boolean getHadDiffcultTreat() {
        return hadDiffcultTreat;
    }

    /**
     * 设置是否有使用困难或不舒适的经历
     *
     * @param hadDiffcultTreat 是否有使用困难或不舒适的经历
     */
    public void setHadDiffcultTreat(Boolean hadDiffcultTreat) {
        this.hadDiffcultTreat = hadDiffcultTreat;
    }

    /**
     * 获取缺牙但不修复的原因
     *
     * @return miss_teeth_unrepeat_cause - 缺牙但不修复的原因
     */
    public String getMissTeethUnrepeatCause() {
        return missTeethUnrepeatCause;
    }

    /**
     * 设置缺牙但不修复的原因
     *
     * @param missTeethUnrepeatCause 缺牙但不修复的原因
     */
    public void setMissTeethUnrepeatCause(String missTeethUnrepeatCause) {
        this.missTeethUnrepeatCause = missTeethUnrepeatCause;
    }

    /**
     * 获取是否正畸治疗
     *
     * @return had_orthodontic - 是否正畸治疗
     */
    public Boolean getHadOrthodontic() {
        return hadOrthodontic;
    }

    /**
     * 设置是否正畸治疗
     *
     * @param hadOrthodontic 是否正畸治疗
     */
    public void setHadOrthodontic(Boolean hadOrthodontic) {
        this.hadOrthodontic = hadOrthodontic;
    }

    /**
     * 获取正畸治疗开始日期
     *
     * @return orthodontic_start_date - 正畸治疗开始日期
     */
    public Date getOrthodonticStartDate() {
        return orthodonticStartDate;
    }

    /**
     * 设置正畸治疗开始日期
     *
     * @param orthodonticStartDate 正畸治疗开始日期
     */
    public void setOrthodonticStartDate(Date orthodonticStartDate) {
        this.orthodonticStartDate = orthodonticStartDate;
    }

    /**
     * 获取正畸治疗结束日期
     *
     * @return orthodontic_end_date - 正畸治疗结束日期
     */
    public Date getOrthodonticEndDate() {
        return orthodonticEndDate;
    }

    /**
     * 设置正畸治疗结束日期
     *
     * @param orthodonticEndDate 正畸治疗结束日期
     */
    public void setOrthodonticEndDate(Date orthodonticEndDate) {
        this.orthodonticEndDate = orthodonticEndDate;
    }

    /**
     * 获取是否接受过口腔卫生宣教
     *
     * @return had_hygiene_education - 是否接受过口腔卫生宣教
     */
    public Boolean getHadHygieneEducation() {
        return hadHygieneEducation;
    }

    /**
     * 设置是否接受过口腔卫生宣教
     *
     * @param hadHygieneEducation 是否接受过口腔卫生宣教
     */
    public void setHadHygieneEducation(Boolean hadHygieneEducation) {
        this.hadHygieneEducation = hadHygieneEducation;
    }

    /**
     * 获取是否用过菌斑染色体
     *
     * @return used_plaque_dna - 是否用过菌斑染色体
     */
    public Boolean getUsedPlaqueDna() {
        return usedPlaqueDna;
    }

    /**
     * 设置是否用过菌斑染色体
     *
     * @param usedPlaqueDna 是否用过菌斑染色体
     */
    public void setUsedPlaqueDna(Boolean usedPlaqueDna) {
        this.usedPlaqueDna = usedPlaqueDna;
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