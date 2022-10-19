package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 简介:
 *
 *@author: ylx
 *@date: 2022/5/13
 *@description:
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "banner管理VO")
public class BannerVO {

   @ApiModelProperty(value = "id")
    private Integer id;

    /**
     * banner图片地址
     */
    @ApiModelProperty(value = "banner图片地址")
    private String imageUrl;


    /**
     * 链接类型
     */
    @ApiModelProperty(value = "链接类型  0空白1产品 2文章 3.口腔服务")
    private Integer linkType;

    /**
     * 链接内容
     */
    @ApiModelProperty(value = "链接内容")
    private String linkContext;

    /**
     * 绑定跳转的产品id
     */
    @ApiModelProperty(value = "绑定跳转的产品id/文章id")
    private Integer productId;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date crtTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updTime;
}
