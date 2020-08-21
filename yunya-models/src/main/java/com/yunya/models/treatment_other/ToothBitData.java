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
     * 影像id
     */
    @Column(name = "photo_service_id")
    private Integer photoServiceId;

    /**
     * 牙位id
     */
    @Column(name = "tooth_bit")
    private String toothBit;

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
     * 获取影像id
     *
     * @return photo_service_id - 影像id
     */
    public Integer getPhotoServiceId() {
        return photoServiceId;
    }

    /**
     * 设置影像id
     *
     * @param photoServiceId 影像id
     */
    public void setPhotoServiceId(Integer photoServiceId) {
        this.photoServiceId = photoServiceId;
    }

    /**
     * 获取牙位id
     *
     * @return tooth_bit - 牙位id
     */
    public String getToothBit() {
        return toothBit;
    }

    /**
     * 设置牙位id
     *
     * @param toothBit 牙位id
     */
    public void setToothBit(String toothBit) {
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
}