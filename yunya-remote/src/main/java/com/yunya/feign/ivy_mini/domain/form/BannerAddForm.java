package com.yunya.feign.ivy_mini.domain.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/13
 * @description:
 */
@Data
public class BannerAddForm {
    private Integer id;
    @ApiModelProperty(value = "图片地址")
    @NotNull(message = "图片地址不能为空")
    private String imageUrl;
    @ApiModelProperty(value = "链接类型 0空白1产品 2文章")
    @NotNull(message = "链接类型不能为空")
    private Integer linkType;
    @ApiModelProperty(value = "绑定跳转的产品id/文章id(链接内容)")
    private Integer productId;
}
