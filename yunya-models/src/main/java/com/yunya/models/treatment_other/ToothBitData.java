package com.yunya.models.treatment_other;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tooth_bit_data")
public class ToothBitData {
    @Id
    private Integer id;

    /**
     * 牙位id
     */
    @Column(name = "tooth_bit")
    private Integer toothBit;

    /**
     * 图片名称
     */
    @Column(name = "film_name")
    private String filmName;

    /**
     * 牙位id uri
     */
    @Column(name = "tooth_bit_film")
    private String toothBitFilm;

    /**
     * 牙位图片上传时间
     */
    @Column(name = "upload_time")
    private Date uploadTime;

    /**
     * 创建者id
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 修改者id
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 修改时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 患者id
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 组织id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 就诊id
     */
    @Column(name = "treatment_record_id")
    private Integer treatmentRecordId;

    /**
     * 牙医id
     */
    @Column(name = "dentist_id")
    private Integer dentistId;

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
     * 获取牙位id
     *
     * @return tooth_bit - 牙位id
     */
    public Integer getToothBit() {
        return toothBit;
    }

    /**
     * 设置牙位id
     *
     * @param toothBit 牙位id
     */
    public void setToothBit(Integer toothBit) {
        this.toothBit = toothBit;
    }

    /**
     * 获取图片名称
     *
     * @return film_name - 图片名称
     */
    public String getFilmName() {
        return filmName;
    }

    /**
     * 设置图片名称
     *
     * @param filmName 图片名称
     */
    public void setFilmName(String filmName) {
        this.filmName = filmName;
    }

    /**
     * 获取牙位id uri
     *
     * @return tooth_bit_film - 牙位id uri
     */
    public String getToothBitFilm() {
        return toothBitFilm;
    }

    /**
     * 设置牙位id uri
     *
     * @param toothBitFilm 牙位id uri
     */
    public void setToothBitFilm(String toothBitFilm) {
        this.toothBitFilm = toothBitFilm;
    }

    /**
     * 获取牙位图片上传时间
     *
     * @return upload_time - 牙位图片上传时间
     */
    public Date getUploadTime() {
        return uploadTime;
    }

    /**
     * 设置牙位图片上传时间
     *
     * @param uploadTime 牙位图片上传时间
     */
    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    /**
     * 获取创建者id
     *
     * @return crt_id - 创建者id
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置创建者id
     *
     * @param crtId 创建者id
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
     * 获取修改者id
     *
     * @return upd_id - 修改者id
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * 设置修改者id
     *
     * @param updId 修改者id
     */
    public void setUpdId(Integer updId) {
        this.updId = updId;
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
     * 获取组织id
     *
     * @return org_id - 组织id
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置组织id
     *
     * @param orgId 组织id
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取就诊id
     *
     * @return treatment_record_id - 就诊id
     */
    public Integer getTreatmentRecordId() {
        return treatmentRecordId;
    }

    /**
     * 设置就诊id
     *
     * @param treatmentRecordId 就诊id
     */
    public void setTreatmentRecordId(Integer treatmentRecordId) {
        this.treatmentRecordId = treatmentRecordId;
    }

    /**
     * 获取牙医id
     *
     * @return dentist_id - 牙医id
     */
    public Integer getDentistId() {
        return dentistId;
    }

    /**
     * 设置牙医id
     *
     * @param dentistId 牙医id
     */
    public void setDentistId(Integer dentistId) {
        this.dentistId = dentistId;
    }
}