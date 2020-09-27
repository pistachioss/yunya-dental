package com.yunya.models.patient_central;

import java.util.Date;
import javax.persistence.*;

/**
 * @author WY
 */
@Table(name = "patient_img")
public class PatientImg {
    /**
     * 主键
     */
    @Id
    private Integer id;

    /**
     * 患者id
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 备注 备注
     */
    private String remarks;

    /**
     * 是否有效 是否有效
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
     * 患者图1（头像）
     */
    @Column(name = "img_one")
    private String imgOne;

    /**
     * 患者图2
     */
    @Column(name = "img_two")
    private String imgTwo;

    /**
     * 患者图3
     */
    @Column(name = "img_three")
    private String imgThree;

    /**
     * 设备照片图1id
     */
    @Column(name = "face_id_one")
    private String faceIdOne;

    /**
     * 设备照片图2id
     */
    @Column(name = "face_id_two")
    private String faceIdTwo;

    /**
     * 设备照片图3id
     */
    @Column(name = "face_id_three")
    private String faceIdThree;

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
     * 获取是否有效 是否有效
     *
     * @return inservice - 是否有效 是否有效
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否有效 是否有效
     *
     * @param inservice 是否有效 是否有效
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

    /**
     * 获取患者图1（头像）
     *
     * @return img_one - 患者图1（头像）
     */
    public String getImgOne() {
        return imgOne;
    }

    /**
     * 设置患者图1（头像）
     *
     * @param imgOne 患者图1（头像）
     */
    public void setImgOne(String imgOne) {
        this.imgOne = imgOne;
    }

    /**
     * 获取患者图2
     *
     * @return img_two - 患者图2
     */
    public String getImgTwo() {
        return imgTwo;
    }

    /**
     * 设置患者图2
     *
     * @param imgTwo 患者图2
     */
    public void setImgTwo(String imgTwo) {
        this.imgTwo = imgTwo;
    }

    /**
     * 获取患者图3
     *
     * @return img_three - 患者图3
     */
    public String getImgThree() {
        return imgThree;
    }

    /**
     * 设置患者图3
     *
     * @param imgThree 患者图3
     */
    public void setImgThree(String imgThree) {
        this.imgThree = imgThree;
    }

    /**
     * 获取患者图 1 id
     * @return faceIdOne
     */
    public String getFaceIdOne() {
        return faceIdOne;
    }

    /**
     * 设置患者图 1 id
     * @param faceIdOne 患者图1
     */
    public void setFaceIdOne(String faceIdOne) {
        this.faceIdOne = faceIdOne;
    }

    /**
     * 获取患者图 2
     * @return faceIdTwo 患者图 2
     */
    public String getFaceIdTwo() {
        return faceIdTwo;
    }

    /**
     * 设置患者图 2 id
     * @param faceIdTwo 患者图2
     */
    public void setFaceIdTwo(String faceIdTwo) {
        this.faceIdTwo = faceIdTwo;
    }

    /**
     * 获取患者图 3 id
     * @return
     */
    public String getFaceIdThree() {
        return faceIdThree;
    }

    /**
     * 获取患者图 4 id
     * @param faceIdThree 患者图4 id
     */
    public void setFaceIdThree(String faceIdThree) {
        this.faceIdThree = faceIdThree;
    }
}