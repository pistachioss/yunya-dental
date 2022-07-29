package com.yunya.feign.ivy_mini.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @description:
 * @author: xy
 * @date 2021/12/17 15:42
 **/
@Data
@ApiModel(value = "小程序授权登录")
public class WeChatLoginForm {
    @ApiModelProperty(value = "临时登录凭证code", required = true)
    @NotBlank
    private String code;
    @ApiModelProperty(value = "微信用户信息")
    private WxUserInfoForm userInfo;
}
