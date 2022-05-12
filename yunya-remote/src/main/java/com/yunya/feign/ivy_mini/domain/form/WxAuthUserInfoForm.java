package com.yunya.feign.ivy_mini.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @description:
 * @author: xy
 * @date 2021/12/20 15:41
 **/
@Data
@ApiModel(value = "微信手机授权信息参数")
public class WxAuthUserInfoForm {
    @ApiModelProperty(value = "code", required = true)
    private String code;
    @ApiModelProperty(value = "加密数据", required = true)
    @NotBlank
    private String encryptedData;
    @ApiModelProperty(value = "加密算法的初始向量", required = true)
    @NotBlank
    private String iv;
}
