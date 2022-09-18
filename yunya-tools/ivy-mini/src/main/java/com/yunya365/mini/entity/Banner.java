package com.yunya365.mini.entity;

import lombok.Data;

import java.util.Date;
import javax.persistence.*;
@Data
public class Banner {
    @Id
    private Integer id;

    /**
     * 广告图片地址
     */
    @Column(name = "image_url")
    private String imageUrl;

    /**
     * 广告链接地址
     */
    @Column(name = "ad_link")
    private String adLink;

    /**
     * 链接类型
     */
    @Column(name = "link_type")
    private Integer linkType;

    /**
     * 链接内容
     */
    @Column(name = "link_context")
    private String linkContext;

    /**
     * 绑定跳转的产品id
     */
    @Column(name = "product_id")
    private Integer productId;



    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;


}