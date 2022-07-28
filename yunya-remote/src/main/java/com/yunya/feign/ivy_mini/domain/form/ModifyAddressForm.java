package com.yunya.feign.ivy_mini.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * @description:
 * @author: xy
 * @date 2022/5/20 13:16
 **/
@Data
@ApiModel(value = "修改收货地址参数")
public class ModifyAddressForm {

    @ApiModelProperty(value = "id", required = true)
    @NotNull
    private Integer id;
    @ApiModelProperty(value = "收货人", required = true)
    @NotBlank
    @Size(max = 10)
    private String name;
    @ApiModelProperty(value = "收货人电话", required = true)
    @NotBlank
    @Pattern(regexp = "^(13[0-9]|14[0,1,4-9]|15[0-3,5-9]|16[2,5,6,7]|17[0-8]|18[0-9]|19[0-3,5-9])\\d{8}$", message = "手机号格式错误")
    private String phoneNumber;
    @ApiModelProperty(value = "邮编")
    private String postCode;
    @ApiModelProperty(value = "省份/直辖市", required = true)
    @NotBlank
    private String province;
    @ApiModelProperty(value = "城市", required = true)
    @NotBlank
    private String city;
    @ApiModelProperty(value = "区", required = true)
    @NotBlank
    private String region;
    @ApiModelProperty(value = "详细地址(街道)", required = true)
    @NotBlank
    private String detailAddress;
    @ApiModelProperty(value = "标签")
    private String tag;
    @ApiModelProperty(value = "是否为默认 0-否 1-是")
    private Boolean defaultStatus;
}
