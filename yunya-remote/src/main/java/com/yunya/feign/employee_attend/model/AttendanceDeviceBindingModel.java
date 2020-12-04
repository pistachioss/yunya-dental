package com.yunya.feign.employee_attend.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * 简介：考勤设备绑定模型
 *
 * @author: chenlin
 * @Description: 考勤设备绑定模型
 * @Date: 2020/11/6 10:17
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("考勤设备绑定模型")
public class AttendanceDeviceBindingModel {

    /** 主键id */
    @ApiModelProperty(value = "主键id")
    private Integer id;

    /** 用户id */
    @ApiModelProperty(value = "用户id", required = true)
    @NotNull(message = "用户id不能为空")
    private Integer userId;

    /** 绑定打卡账号 */
    @ApiModelProperty(value = "绑定打卡账号", required = true)
    @NotBlank(message = "绑定打卡账号不能为空")
    @Pattern(regexp = "^(13[0-9]|14[5|7]|15[0|1|2|3|4|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$")
    private String mobile;

    /** 验证码 */
    @ApiModelProperty(value = "验证码", required = true)
    @NotBlank(message = "验证码不能为空")
    private String verifyCode;

    /** 绑定状态：0：已解绑 1：已绑定 */
    @ApiModelProperty("绑定状态：0：已解绑 1：已绑定")
    private Integer bindingStatus;

    /** 初始化设备号 */
    @ApiModelProperty("初始化设备号")
    private String firstNumber;

    /** 绑定时间 */
    @ApiModelProperty("绑定时间")
    private Date bindingTime;

    /** 设备号 */
    @ApiModelProperty(value = "设备号", required = true)
    @NotBlank(message = "设备号不能为空")
    private String deviceNumber;

    /** 设备号 */
    @ApiModelProperty("上次绑定设备号")
    private String oldNumber;
}
