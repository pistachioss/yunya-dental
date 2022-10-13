package com.yunya365.mini.entity;

import lombok.Data;

import java.util.Date;
import javax.persistence.*;
@Data
@Table(name = "expert_introduction")
public class ExpertIntroduction {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;



    /**
     * 专家名称
     */
    private String name;

    /**
     * 称谓
     */
    private String appellation;

    /**
     * 资质荣誉
     */
    private String honor;

    /**
     * 专业擅长
     */
    private String expertise;

    /**
     * 专家形象图片地址
     */
    @Column(name = "picture_address")
    private String pictureAddress;
    @Column(name = "visit_clinic")
    private String visitClinic;
    @Column(name = "visit_time")
    private String visitTime;

    /**
     * 是否置顶 0否 1是
     */
    @Column(name = "is_sort")
    private Integer isSort;

    /**
     * 发布状态 0否1是
     */
    private Integer status;

    /**
     * 创建人ID
     */
    @Column(name = "crt_id")
    private Integer crtId;
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

    /**
     * 获取专家名称
     *
     * @return name - 专家名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置专家名称
     *
     * @param name 专家名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取称谓
     *
     * @return appellation - 称谓
     */
    public String getAppellation() {
        return appellation;
    }

    /**
     * 设置称谓
     *
     * @param appellation 称谓
     */
    public void setAppellation(String appellation) {
        this.appellation = appellation;
    }

    /**
     * 获取资质荣誉
     *
     * @return honor - 资质荣誉
     */
    public String getHonor() {
        return honor;
    }

    /**
     * 设置资质荣誉
     *
     * @param honor 资质荣誉
     */
    public void setHonor(String honor) {
        this.honor = honor;
    }

    /**
     * 获取专业擅长
     *
     * @return expertise - 专业擅长
     */
    public String getExpertise() {
        return expertise;
    }

    /**
     * 设置专业擅长
     *
     * @param expertise 专业擅长
     */
    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    /**
     * 获取专家形象图片地址
     *
     * @return picture_address - 专家形象图片地址
     */
    public String getPictureAddress() {
        return pictureAddress;
    }

    /**
     * 设置专家形象图片地址
     *
     * @param pictureAddress 专家形象图片地址
     */
    public void setPictureAddress(String pictureAddress) {
        this.pictureAddress = pictureAddress;
    }

    /**
     * 获取是否置顶 0否 1是
     *
     * @return is_sort - 是否置顶 0否 1是
     */
    public Integer getIsSort() {
        return isSort;
    }

    /**
     * 设置是否置顶 0否 1是
     *
     * @param isSort 是否置顶 0否 1是
     */
    public void setIsSort(Integer isSort) {
        this.isSort = isSort;
    }

    /**
     * 获取发布状态 0否1是
     *
     * @return status - 发布状态 0否1是
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置发布状态 0否1是
     *
     * @param status 发布状态 0否1是
     */
    public void setStatus(Integer status) {
        this.status = status;
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