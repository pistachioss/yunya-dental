package com.yunya.feign.wechat.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @description:
 * @author: xy
 * @date 2021/4/2 17:42
 **/
@ApiModel(value = "微信公众号会员注册请求参数")
@Data
public class WxRegisterModel {
    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank
    private String userName;
    @ApiModelProperty(value = "手机号码", required = true)
    @Pattern(regexp = "^(13[0-9]|14[0,1,4-9]|15[0-3,5-9]|16[2,5,6,7]|17[0-8]|18[0-9]|19[0-3,5-9])\\d{8}$", message = "手机号码格式不正确")
    @NotBlank
    private String mobile;

}
