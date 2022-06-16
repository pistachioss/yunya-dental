package com.yunya.feign.ivy_mini.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/6/16
 * @description:
 */
@Data
@ApiModel(value = "修改取货人参数")
public class FansPickUpForm {

    @ApiModelProperty(value = "Id")
    private Integer id;
    @ApiModelProperty(value = "取货人名称", required = true)
    @NotBlank
    @Size(max = 10)
    private String name;
    @ApiModelProperty(value = "取货人电话", required = true)
    @NotBlank
    @Pattern(regexp = "^(13[0-9]|14[0,1,4-9]|15[0-3,5-9]|16[2,5,6,7]|17[0-8]|18[0-9]|19[0-3,5-9])\\d{8}$", message = "手机号格式错误")
    private String phoneNumber;
    @ApiModelProperty(value = "是否为默认 0-否 1-是")
    private Integer defaultStatus;
}
