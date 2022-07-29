package com.yunya.feign.ivy_mini.domain.vo;

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
@ApiModel(value = "用户详情")
public class FansDetailVO {
    @ApiModelProperty(value = "openId")
    private String openId;
    @ApiModelProperty(value = "头像")
    private String headImgurl;
    @ApiModelProperty(value = "昵称")
    private String nickName;
    @ApiModelProperty(value = "性别：1男性，2女性，0未知")
    private Short sex;
    @ApiModelProperty(value = "生日")
    private Date birthday;
    @ApiModelProperty(value = "用户手机")
    private String phoneNumber;
}
