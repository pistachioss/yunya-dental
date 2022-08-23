package com.yunya.feign.ivy_mini.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.Date;

/**
 * @description:
 * @author: xy
 * @date 2022/5/20 13:16
 **/
@Data
@ApiModel(value = "编辑用户参数")
public class ModifyFansForm {
    @ApiModelProperty(value = "openId", required = true)
    @NotBlank
    private String openId;
    @ApiModelProperty(value = "头像", required = true)
    @NotBlank
    private String headImgurl;
    @ApiModelProperty(value = "昵称", required = true)
    @NotBlank
    private String nickName;
    @ApiModelProperty(value = "性别：1男性，2女性，0未知", required = true)
    @NotNull
    private Short sex;
    @ApiModelProperty(value = "生日", required = true)
    @NotNull
    private Date birthday;
    @ApiModelProperty(value = "用户手机", required = true)
    @NotBlank
    @Pattern(regexp = "^(13[0-9]|14[0,1,4-9]|15[0-3,5-9]|16[2,5,6,7]|17[0-8]|18[0-9]|19[0-3,5-9])\\d{8}$", message = "手机号格式错误")
    private String phoneNumber;
}
