package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/10
 * @description: 文章管理VO
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "文章管理VO")
public class ArticleVO implements Serializable {

    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 文章链接
     */
    @ApiModelProperty(value = "文章链接")
    private String articleUrl;

    /**
     * 封面图
     */
    @ApiModelProperty(value = "封面图")
    private String coverPicture;

    /**
     * 分类 1：艾维动态 2口腔科普
     */
    @ApiModelProperty(value = "分类 1：艾维动态 2口腔科普")
    private Integer type;

    @ApiModelProperty(value = "分类Id")
    private Integer typeId;

    /**
     * 是否置顶 0否 1是'
     */
    @ApiModelProperty(value = "是否置顶 0否 1是'")
    private Integer isSort;
    @ApiModelProperty(value = "置顶排序")
    private Integer sortNum;

    /**
     * 阅读数
     */
    @ApiModelProperty(value = "阅读数")
    private Integer readingNumber;

    /**
     * 发布状态 0否1是
     */
    @ApiModelProperty(value = "发布状态 0否1是")
    private Integer status;

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
