package com.yunya365.mini.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import javax.persistence.*;
@Data
public class recommed {
    @ApiModelProperty(value = "id")
    private Integer id;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    private String title;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String describes;

    /**
     * 图片链接
     */
    @ApiModelProperty(value = "图片链接")
    @Column(name = "image_url")
    private String imageUrl;

    /**
     * 链接类型
     */
    @ApiModelProperty(value = "链接类型 0空白1产品 2文章 3.口腔服务")
    @Column(name = "link_type")
    private Integer linkType;
    /**
     * 绑定跳转的产品id
     */
    @ApiModelProperty(value = "绑定跳转的产品id")
    @Column(name = "product_id")
    private Integer productId;
    /**
     * 链接内容
     */
    @ApiModelProperty(value = "链接内容")
    @Column(name = "url_context")
    private String urlContext;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @Column(name = "upd_time")
    private Date updTime;


}