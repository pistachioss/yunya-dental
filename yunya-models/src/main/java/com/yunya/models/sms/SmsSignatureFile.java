package com.yunya.models.sms;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "sms_signature_file")
public class SmsSignatureFile {
    @Id
    private Integer id;

    /**
     * 签名id
     */
    @Column(name = "signature_id")
    private Integer signatureId;

    /**
     * 文件格式：jpg、png、gif、jpeg
     */
    @Column(name = "file_type")
    private String fileType;

    /**
     * 文件存储位置
     */
    @Column(name = "file_url")
    private String fileUrl;

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
     * 修改人id
     */
    @Column(name = "upt_id")
    private Integer uptId;

    /**
     * 修改时间
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
     * 获取签名id
     *
     * @return signature_id - 签名id
     */
    public Integer getSignatureId() {
        return signatureId;
    }

    /**
     * 设置签名id
     *
     * @param signatureId 签名id
     */
    public void setSignatureId(Integer signatureId) {
        this.signatureId = signatureId;
    }

    /**
     * 获取文件格式：jpg、png、gif、jpeg
     *
     * @return file_type - 文件格式：jpg、png、gif、jpeg
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * 设置文件格式：jpg、png、gif、jpeg
     *
     * @param fileType 文件格式：jpg、png、gif、jpeg
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /**
     * 获取文件存储位置
     *
     * @return file_url - 文件存储位置
     */
    public String getFileUrl() {
        return fileUrl;
    }

    /**
     * 设置文件存储位置
     *
     * @param fileUrl 文件存储位置
     */
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
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
     * 获取修改人id
     *
     * @return upt_id - 修改人id
     */
    public Integer getUptId() {
        return uptId;
    }

    /**
     * 设置修改人id
     *
     * @param uptId 修改人id
     */
    public void setUptId(Integer uptId) {
        this.uptId = uptId;
    }

    /**
     * 获取修改时间
     *
     * @return upt_time - 修改时间
     */
    public Date getUptTime() {
        return uptTime;
    }

    /**
     * 设置修改时间
     *
     * @param uptTime 修改时间
     */
    public void setUptTime(Date uptTime) {
        this.uptTime = uptTime;
    }
}