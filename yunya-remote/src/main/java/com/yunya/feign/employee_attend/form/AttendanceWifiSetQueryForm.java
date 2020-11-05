package com.yunya.feign.employee_attend.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 简介：考勤Wifi设置查询参数模型
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/5 9:23
 * @since: 1.0.0
 */
@ApiModel(value = "考勤Wifi设置查询参数模型")
@Data
@ToString
public class AttendanceWifiSetQueryForm implements Serializable {

    @ApiModelProperty("是否分页,默认true")
    private Boolean whetherPage = true;

    @ApiModelProperty("页码，默认第一页")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示条数，默认10条")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    /** 组织id */
    @ApiModelProperty(value = "组织ID")
    private Integer orgId;

    /** mac地址 */
    @ApiModelProperty(value = "mac地址")
    private String macAddress;
}
