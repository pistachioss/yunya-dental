package com.yunya.feign.ivy_mini.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/12
 * @description:
 */
@Data
@ApiModel(value = "推荐专区")
public class recommedForm {


    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    @NotNull(message = "标题不能为空！")
    private String title;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    @NotNull(message = "描述不能为空！")
    private String describes;

    /**
     * 图片链接
     */
    @ApiModelProperty(value = "图片链接")
    @NotNull(message = "图片链接不能为空！")
    private String imageUrl;

    /**
     * 链接类型
     */
    @ApiModelProperty(value = "链接类型 0空白1产品 2文章")
    @NotNull(message = "链接类型不能为空！")
    private Integer linkType;
    /**
     * 绑定跳转的产品id
     */
    @ApiModelProperty(value = "绑定跳转的产品id/文章id (链接内容)")
    private Integer productId;
    /**
     * 链接内容
     */
    @ApiModelProperty(value = "链接内容 当前版本该字段无用 推荐专区都为绑定到指定的产品或文章 没有自定义链接")
    private String urlContext;
}
