package com.yunya.feign.ivy_mini.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * @description:
 * @author: xy
 * @date 2021/11/18 13:05
 **/
@Data
@ApiModel(value = "完善资料用户参数")
public class CompleteMemberForm {
    @ApiModelProperty(value = "会员id", required = true)
    @NotNull
    @Min(1)
    private Integer memberId;
    @ApiModelProperty(value = "头像")
    private String headImgurl;
    @ApiModelProperty(value = "昵称", required = true)
    @NotBlank
    private String nickName;
    @ApiModelProperty(value = "性别（0-未知 1-男性 2-女性）", required = true)
    @NotNull
    private Integer sex;
    @ApiModelProperty(value = "学校")
    @NotNull
    @Min(1)
    private Integer collegeCampusId;
    @ApiModelProperty(value = "专业")
    @NotNull
    @Min(1)
    private Integer majorId;
    @ApiModelProperty(value = "国家")
    private String country;
    @ApiModelProperty(value = "省份")
    private String province;
    @ApiModelProperty(value = "城市")
    private String city;
    @ApiModelProperty(value = "地址")
    private String address;
    @ApiModelProperty(value = "经度")
    private Double longitude;
    @ApiModelProperty(value = "纬度")
    private Double latitude;
    @ApiModelProperty(value = "语言")
    private String language;
}
