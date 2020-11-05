package com.yunya.feign.employee_attend.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：考勤Wifi设置响应
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/5 12:59
 * @since: 1.0.0
 */
@ApiModel("考勤Wifi设置响应")
@Data
@ToString
public class AttendanceWifiSetVO implements Serializable {
    /** 主键id */
    @ApiModelProperty(value = "主键id")
    private Integer id;

    /** 组织id */
    @ApiModelProperty(value = "组织id")
    private Integer orgId;

    /** 组织名称 */
    @ApiModelProperty(value = "组织名称")
    private String organizationName;

    /** 考勤地址 */
    @ApiModelProperty(value = "wifi名称")
    private String wifiName;

    /** 经度 */
    @ApiModelProperty(value = "mac地址")
    private String macAddress;

}
