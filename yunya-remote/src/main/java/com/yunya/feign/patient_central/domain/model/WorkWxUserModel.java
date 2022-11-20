package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/7/6
 * @description:
 */
@Data
@ApiModel("企业微信客户参数")
public class WorkWxUserModel {
    @ApiModelProperty(value = "手机号")
    private String mobile;
    @ApiModelProperty(value = "用户昵称")
    private String nickName;
    @ApiModelProperty(value = "用户头像")
    private String headImgurl;
    @ApiModelProperty(value = "生日")
    private Date birthday;
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
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "地址")
    private String address;
    @ApiModelProperty(value = "用户来源（0-公众号 1-小程序 2-企业微信）", required = true)
    @NotNull
    private Integer sourceType;
    @ApiModelProperty(value = "用户来源名称", required = true)
    @NotBlank
    private String sourceTypeName;
    @ApiModelProperty(value = "公众号/小程序：openid，企业微信：external_userid", required = true)
    @NotBlank
    private String openId;
    @ApiModelProperty(value = "微信开放平台的唯一身份标识", required = true)
    @NotBlank
    private String unionId;
}
