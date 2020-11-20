package com.yunya.feign.treatment_other.domain.model;

import java.util.Date;
import javax.persistence.*;

@Table(name = "x_ray_film")
public class XRayFilmModel {
    /**
     * 主键
     */
    @Id
    private Integer id;

    /**
     * 患者ID
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * X-光片类型 0-照片；1-根尖片；2-全景片；3-正位片；4-侧位片；5-关节片；6-正畸片；7-其他片
     */
    private Byte type;

    /**
     * 牙位编号
     */
    @Column(name = "tooth_no")
    private Integer toothNo;

    /**
     * 图片资源定位路径
     */
    private String url;

    /**
     * 图片名
     */
    @Column(name = "photo_name")
    private String photoName;

    /**
     * 上传时间
     */
    @Column(name = "upload_time")
    private Date uploadTime;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 是否有效
     */
    private String inservice;

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
     * 更新人
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
     * 获取X-光片类型 0-照片；1-根尖片；2-全景片；3-正位片；4-侧位片；5-关节片；6-正畸片；7-其他片
     *
     * @return type - X-光片类型 0-照片；1-根尖片；2-全景片；3-正位片；4-侧位片；5-关节片；6-正畸片；7-其他片
     */
    public Byte getType() {
        return type;
    }

    /**
     * 设置X-光片类型 0-照片；1-根尖片；2-全景片；3-正位片；4-侧位片；5-关节片；6-正畸片；7-其他片
     *
     * @param type X-光片类型 0-照片；1-根尖片；2-全景片；3-正位片；4-侧位片；5-关节片；6-正畸片；7-其他片
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * 获取图片资源定位路径
     *
     * @return url - 图片资源定位路径
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置图片资源定位路径
     *
     * @param url 图片资源定位路径
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取图片名
     *
     * @return photo_name - 图片名
     */
    public String getPhotoName() {
        return photoName;
    }

    /**
     * 设置图片名
     *
     * @param photoName 图片名
     */
    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    /**
     * 获取上传时间
     *
     * @return upload_time - 上传时间
     */
    public Date getUploadTime() {
        return uploadTime;
    }

    /**
     * 设置上传时间
     *
     * @param uploadTime 上传时间
     */
    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    /**
     * 获取备注
     *
     * @return remarks - 备注
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * 设置备注
     *
     * @param remarks 备注
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * 获取是否有效
     *
     * @return inservice - 是否有效
     */
    public String getInservice() {
        return inservice;
    }

    /**
     * 设置是否有效
     *
     * @param inservice 是否有效
     */
    public void setInservice(String inservice) {
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
     * 获取更新人
     *
     * @return upd_name - 更新人
     */
    public String getUpdName() {
        return updName;
    }

    /**
     * 设置更新人
     *
     * @param updName 更新人
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
     * 获取牙位编号
     * @return toothNo 牙位编号
     */
    public Integer getToothNo() {
        return toothNo;
    }

    /**
     * 设置牙位编号
     * @param toothNo 牙位编号
     */
    public void setToothNo(Integer toothNo) {
        this.toothNo = toothNo;
    }
}