package com.yunya.feign.ivy_mini.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @description:
 * @author: xy
 * @date 2021/11/16 15:17
 **/
@Data
@ApiModel(value = "患者绑定验证码校验参数")
public class SmsCaptchaCheckForm {
    @ApiModelProperty(value = "手机号", required = true)
    @Pattern(regexp = "^(13[0-9]|14[0,1,4-9]|15[0-3,5-9]|16[2,5,6,7]|17[0-8]|18[0-9]|19[0-3,5-9])\\d{8}$", message = "手机号格式错误")
    @NotBlank
    private String phoneNumber;
    @ApiModelProperty(value = "验证码", required = true)
    @NotBlank
    private String captcha;
    @ApiModelProperty(value = "患者id")
    private Integer patientId;
}
