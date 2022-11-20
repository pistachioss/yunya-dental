package com.yunya365.mini.entity;

import lombok.Data;

import java.util.Date;
import javax.persistence.*;
@Data
public class Article {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    private String name;

    /**
     * 文章链接
     */
    @Column(name = "article_url")
    private String articleUrl;

    /**
     * 封面图
     */
    @Column(name = "cover_picture")
    private String coverPicture;

    /**
     * 分类 1：艾维动态 2口腔科普
     */
    private Integer type;

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    /**
     * 分类Id
     */
    @Column(name = "type_id")
    private Integer typeId;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 排序
     */
    private Integer sort;
    @Column(name = "sort_num")
    private Integer sortNum;

    /**
     * 阅读数
     */
    @Column(name = "reading_number")
    private Integer readingNumber;

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
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取文章链接
     *
     * @return article_url - 文章链接
     */
    public String getArticleUrl() {
        return articleUrl;
    }

    /**
     * 设置文章链接
     *
     * @param articleUrl 文章链接
     */
    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    /**
     * 获取封面图
     *
     * @return cover_picture - 封面图
     */
    public String getCoverPicture() {
        return coverPicture;
    }

    /**
     * 设置封面图
     *
     * @param coverPicture 封面图
     */
    public void setCoverPicture(String coverPicture) {
        this.coverPicture = coverPicture;
    }

    /**
     * 获取分类 1：艾维动态 2口腔科普
     *
     * @return type - 分类 1：艾维动态 2口腔科普
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置分类 1：艾维动态 2口腔科普
     *
     * @param type 分类 1：艾维动态 2口腔科普
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取品牌
     *
     * @return brand - 品牌
     */
    public String getBrand() {
        return brand;
    }

    /**
     * 设置品牌
     *
     * @param brand 品牌
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * 获取排序
     *
     * @return sort - 排序
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * 设置排序
     *
     * @param sort 排序
     */
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    /**
     * 获取阅读数
     *
     * @return reading_number - 阅读数
     */
    public Integer getReadingNumber() {
        return readingNumber;
    }

    /**
     * 设置阅读数
     *
     * @param readingNumber 阅读数
     */
    public void setReadingNumber(Integer readingNumber) {
        this.readingNumber = readingNumber;
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