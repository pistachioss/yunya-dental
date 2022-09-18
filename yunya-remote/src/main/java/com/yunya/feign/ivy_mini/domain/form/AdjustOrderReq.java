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
public class AdjustOrderReq {
    @ApiModelProperty(value="id")
    @NotNull(message = "id不能为空")
    private Integer id;

    @ApiModelProperty(value = "分类 1：艾维动态 2口腔科普")
    @NotNull(message = "分类不能为空")
    private Integer type;

    @ApiModelProperty(value="排序方式 1上移 2下移")
    @NotNull(message = "排序方式不能为空")
    private Integer operaType;
}
