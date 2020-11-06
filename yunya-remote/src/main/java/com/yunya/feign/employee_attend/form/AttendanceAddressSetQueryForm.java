package com.yunya.feign.employee_attend.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.Set;

/**
 * 简介：考勤地址设置查询参数模型
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/5 9:23
 * @since: 1.0.0
 */
@ApiModel(value = "考勤地址设置查询参数模型")
@Data
@ToString
public class AttendanceAddressSetQueryForm implements Serializable {

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

    /** 考勤地址 */
    @ApiModelProperty(value = "考勤地址")
    private String attendanceAddress;

    /** 组织ID列表 */
    @ApiModelProperty(value = "组织ID列表")
    private Set<Integer> orgIds;
}
