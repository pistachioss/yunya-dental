package com.yunya.feign.employee_attend.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：考勤地址设置信息
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/5 9:15
 * @since: 1.0.0
 */
@ApiModel(value = "考勤地址设置信息")
@Data
@ToString
public class AttendanceAddressSetVO implements Serializable {
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
    @ApiModelProperty(value = "考勤地址")
    private String attendanceAddress;

    /** 经度 */
    @ApiModelProperty(value = "经度")
    private String longitude;

    /** 纬度 */
    @ApiModelProperty(value = "纬度")
    private String latitude;

    /** 考勤范围 */
    @ApiModelProperty(value = "考勤范围")
    private Integer attendanceRange;
}
