package com.yunya.feign.employee_attend.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：考勤地址设置模型
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/5 12:18
 * @since: 1.0.0
 */
@ApiModel("考勤地址设置模型")
@Data
@ToString
public class AttendanceAddressSetModel implements Serializable {

    /** 主键id */
    @ApiModelProperty(value = "主键id")
    private Integer id;

    /** 组织id */
    @ApiModelProperty(value = "组织id", required = true)
    private Integer orgId;

    /** 考勤地址 */
    @ApiModelProperty(value = "考勤地址", required = true)
    private String attendanceAddress;

    /** 经度 */
    @ApiModelProperty(value = "经度", required = true)
    private String longitude;

    /** 纬度 */
    @ApiModelProperty(value = "纬度", required = true)
    private String latitude;

    /** 考勤范围 */
    @ApiModelProperty(value = "考勤范围", required = true)
    private Integer attendanceRange;
}
