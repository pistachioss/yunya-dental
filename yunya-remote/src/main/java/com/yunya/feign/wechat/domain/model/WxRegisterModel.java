package com.yunya.feign.wechat.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

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
    @NotBlank
    private String mobile;

}
