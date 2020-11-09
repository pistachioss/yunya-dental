package com.yunya.feign.employee_attend.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简介：考勤设置批量添加模型
 *
 * @author: chenlin
 * @Description: 考勤设置批量添加模型
 * @Date: 2020/11/6 18:02
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("考勤设置批量添加模型")
public class AttendanceSetModel implements Serializable {

    /** 考勤地址设置列表 */
    @ApiModelProperty(value = "考勤地址设置列表")
    private List<AttendanceAddressSetModel> attendanceAddressSetModels;

    /** 考勤Wifi设置列表 */
    @ApiModelProperty(value = "考勤Wifi设置列表")
    private List<AttendanceWifiSetModel> attendanceWifiSetModels;
}
