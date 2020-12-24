package com.yunya.feign.sms.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/12/24 14:08
 * @since: 1.0.0
 */
@ApiModel
@ToString
@Data
public class SmsVerifyCodeModel implements Serializable {
    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号",required = true)
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1(3([0-35-9]\\d|4[1-8])|4[14-9]\\d|5([0-35689]\\d|7[1-79])|66\\d|7[2-35-8]\\d|8\\d{2}|9[13589]\\d)\\d{7}$")
    private String mobile;

    /**
     * 短信验证码
     */
    @ApiModelProperty(value = "短信验证码",required = true)
    @NotBlank(message = "短信验证码不能为空")
    private String verifyCode;

    /**
     * 短信自动发送的事件编码
     */
    @ApiModelProperty(value = "短信自动发送的事件编码",required = true)
    @NotBlank(message = "短信自动发送的事件编码不能为空")
    private String eventCode;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id",required = true)
    @NotNull(message = "用户id不能为空")
    private Integer userId;

    /**
     * 用户姓名
     */
    @ApiModelProperty(value = "用户姓名",required = true)
    @NotBlank(message = "用户姓名不能为空")
    private String name;

    /**
     * 组织id
     */
    @ApiModelProperty("组织id")
    @NotNull(message = "组织id不能为空")
    private Integer orgId;
}
