package com.yunya.models.treatment_other;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "photo_service")
public class PhotoService {
    @Id
    private Integer id;

    /**
     * 组织id
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 患者id
     */
    @Column(name = "patient_id")
    private Integer patientId;

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
     * 照片
     */
    @Column(name = "photo_film")
    private String photoFilm;

    /**
     * 根尖片id
     */
    @Column(name = "peiapical_film_id")
    private Integer peiapicalFilmId;

    /**
     * 全景片
     */
    private String panorama;

    /**
     * 正位片
     */
    @Column(name = "normotopia_film")
    private String normotopiaFilm;

    /**
     * 侧位片
     */
    @Column(name = "lateral_film")
    private String lateralFilm;

    /**
     * 关节片
     */
    @Column(name = "joint_film")
    private String jointFilm;

    /**
     * 图片上传日期
     */
    @Column(name = "upload_time")
    private Date uploadTime;

    /**
     * 图片名称
     */
    @Column(name = "film_name")
    private String filmName;

    /**
     * 正畸片
     */
    @Column(name = "orthodontics_film")
    private String orthodonticsFilm;

    /**
     * 其他图片
     */
    @Column(name = "other_film")
    private String otherFilm;

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
     * 最后修改时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 创建人id
     */
    @Column(name = "crt_id")
    private Integer crtId;

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

    /**
     * 获取照片
     *
     * @return photo_film - 照片
     */
    public String getPhotoFilm() {
        return photoFilm;
    }

    /**
     * 设置照片
     *
     * @param photoFilm 照片
     */
    public void setPhotoFilm(String photoFilm) {
        this.photoFilm = photoFilm;
    }

    /**
     * 获取根尖片id
     *
     * @return peiapical_film_id - 根尖片id
     */
    public Integer getPeiapicalFilmId() {
        return peiapicalFilmId;
    }

    /**
     * 设置根尖片id
     *
     * @param peiapicalFilmId 根尖片id
     */
    public void setPeiapicalFilmId(Integer peiapicalFilmId) {
        this.peiapicalFilmId = peiapicalFilmId;
    }

    /**
     * 获取全景片
     *
     * @return panorama - 全景片
     */
    public String getPanorama() {
        return panorama;
    }

    /**
     * 设置全景片
     *
     * @param panorama 全景片
     */
    public void setPanorama(String panorama) {
        this.panorama = panorama;
    }

    /**
     * 获取正位片
     *
     * @return normotopia_film - 正位片
     */
    public String getNormotopiaFilm() {
        return normotopiaFilm;
    }

    /**
     * 设置正位片
     *
     * @param normotopiaFilm 正位片
     */
    public void setNormotopiaFilm(String normotopiaFilm) {
        this.normotopiaFilm = normotopiaFilm;
    }

    /**
     * 获取侧位片
     *
     * @return lateral_film - 侧位片
     */
    public String getLateralFilm() {
        return lateralFilm;
    }

    /**
     * 设置侧位片
     *
     * @param lateralFilm 侧位片
     */
    public void setLateralFilm(String lateralFilm) {
        this.lateralFilm = lateralFilm;
    }

    /**
     * 获取关节片
     *
     * @return joint_film - 关节片
     */
    public String getJointFilm() {
        return jointFilm;
    }

    /**
     * 设置关节片
     *
     * @param jointFilm 关节片
     */
    public void setJointFilm(String jointFilm) {
        this.jointFilm = jointFilm;
    }

    /**
     * 获取图片上传日期
     *
     * @return upload_time - 图片上传日期
     */
    public Date getUploadTime() {
        return uploadTime;
    }

    /**
     * 设置图片上传日期
     *
     * @param uploadTime 图片上传日期
     */
    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
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
     * 获取正畸片
     *
     * @return orthodontics_film - 正畸片
     */
    public String getOrthodonticsFilm() {
        return orthodonticsFilm;
    }

    /**
     * 设置正畸片
     *
     * @param orthodonticsFilm 正畸片
     */
    public void setOrthodonticsFilm(String orthodonticsFilm) {
        this.orthodonticsFilm = orthodonticsFilm;
    }

    /**
     * 获取其他图片
     *
     * @return other_film - 其他图片
     */
    public String getOtherFilm() {
        return otherFilm;
    }

    /**
     * 设置其他图片
     *
     * @param otherFilm 其他图片
     */
    public void setOtherFilm(String otherFilm) {
        this.otherFilm = otherFilm;
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
     * 获取最后修改时间
     *
     * @return upd_time - 最后修改时间
     */
    public Date getUpdTime() {
        return updTime;
    }

    /**
     * 设置最后修改时间
     *
     * @param updTime 最后修改时间
     */
    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
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
}