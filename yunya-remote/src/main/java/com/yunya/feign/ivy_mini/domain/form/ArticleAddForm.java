package com.yunya.feign.ivy_mini.domain.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/11
 * @description:
 */
@Data
public class ArticleAddForm {
    @ApiModelProperty(value = "文章名称")
    @NotNull(message = "文章名称不能为空")
    private String name;
    @ApiModelProperty(value = "文章链接")
    @NotNull(message = "文章链接不能为空")
    private String articleUrl;
    @ApiModelProperty(value = "封面图")
    @NotNull(message = "封面图不能为空")
    private String coverPicture;
    @ApiModelProperty(value = "分类 1：艾维动态 2口腔科普")
    @NotNull(message = "分类不能为空")
    private Integer type;
    @NotNull(message = "分类Id不能为空")
    @ApiModelProperty(value = "分类Id")
    private Integer typeId;

}
