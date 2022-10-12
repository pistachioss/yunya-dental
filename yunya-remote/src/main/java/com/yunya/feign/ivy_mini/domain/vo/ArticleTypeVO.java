package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/10/12
 * @description:
 */@Data
@Accessors(chain = true)
@ApiModel(description = "文章分类管理VO")
public class ArticleTypeVO {

    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "分类归属 1：艾维动态 2口腔科普 3.专家介绍")
    private Integer type;
    @ApiModelProperty(value = "分类名称")
    private String name;
}
