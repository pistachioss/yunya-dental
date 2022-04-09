package com.yunya.feign.employee_attend.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介：考勤相关项目数量VO
 *
 * @author: chenlin
 * @Description: 考勤相关项目数量VO
 * @Date: 2022/3/24 14:37
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("考勤相关项目数量VO")
public class AttendanceItemCountVO implements Serializable {

    /** 请假数量*/
    @ApiModelProperty("请假数量")
    private Integer leaveCount;

    /** 加班数量*/
    @ApiModelProperty("加班数量")
    private Integer workOverCount;

    /** 外勤数量*/
    @ApiModelProperty("外勤数量")
    private Integer fieldCount;

    /** 查询类型：0-查询待审批, 1-查询抄送*/
    @ApiModelProperty(value = "查询类型：0-查询待审批, 1-查询抄送")
    private Integer queryType;
}
