package com.yunya.feign.employee_attend.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 考勤Wifi设置修改参数模型
 *
 * @author: chenlin
 * @date: 2020/11/5 15:42
 * @description:
 * @since: 1.0.0
 */
@ApiModel("考勤Wifi设置修改参数模型")
@Data
@ToString
public class AttendanceWifiSetForm implements Serializable {

    /** 组织ID */
    @ApiModelProperty(value = "组织ID", required = true)
    @NotNull(message = "组织ID不能为空！")
    private Integer orgId;

    /** wifi名称 */
    @NotBlank(message = "wifi名称不能为空！")
    @ApiModelProperty(value = "wifi名称", required = true)
    private String wifiName;

    /** mac地址 */
    @ApiModelProperty(value = "mac地址", required = true)
    @NotNull(message = "mac地址不能为空！")
    private String macAddress;
}
