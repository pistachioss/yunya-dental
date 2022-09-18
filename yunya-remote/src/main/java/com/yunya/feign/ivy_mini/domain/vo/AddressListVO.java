package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: xy
 * @date 2022/5/20 13:16
 **/
@Data
@ApiModel(value = "收货地址列表")
public class AddressListVO {

    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "收货人")
    private String name;
    @ApiModelProperty(value = "收货人电话")
    private String phoneNumber;
    @ApiModelProperty(value = "省份/直辖市")
    private String province;
    @ApiModelProperty(value = "城市")
    private String city;
    @ApiModelProperty(value = "区")
    private String region;
    @ApiModelProperty(value = "详细地址(街道)")
    private String detailAddress;
    @ApiModelProperty(value = "标签")
    private String tag;
    @ApiModelProperty(value = "是否为默认 0-否 1-是")
    private Boolean defaultStatus;
}
