package com.yunya.feign.ivy_mini.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: xy
 * @date 2021/12/20 15:59
 **/
@Data
@ApiModel(value = "微信用户信息 ")
public class WxUserInfoForm {
    @ApiModelProperty(value = "用户昵称")
    private String nickName;
    @ApiModelProperty(value = "用户头像")
    private String avatarUrl;
    @ApiModelProperty(value = "用户性别 0-未知 1-男性 2-女性")
    private Integer gender;
    @ApiModelProperty(value = "用户所在国家")
    private String country;
    @ApiModelProperty(value = "用户所在省份")
    private String province;
    @ApiModelProperty(value = "用户所在城市")
    private String city;
    @ApiModelProperty(value = "语言")
    private String language;
    @ApiModelProperty(value = "经度")
    private Double longitude;
    @ApiModelProperty(value = "纬度")
    private Double latitude;
    @ApiModelProperty(value = "最后一次登录地址")
    private String lastLoginAddress;
    @ApiModelProperty(value = "用户来源（0-公众号 1-小程序）")
    private Integer sourceType;
    @ApiModelProperty(value = "用户来源主体")
    private String sourceTypeName;
}
