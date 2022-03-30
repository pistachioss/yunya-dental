package com.yunya.feign.employee_attend.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介：考勤相关项目数量查询
 *
 * @author: chenlin
 * @Description: 考勤相关项目数量查询
 * @Date: 2022/3/24 14:33
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("考勤相关项目数量查询")
public class AttendanceItemCountQuery implements Serializable {

    /** 查询类型：0-查询待审批, 1-查询抄送*/
    @ApiModelProperty(value = "查询类型：0-查询待审批, 1-查询抄送", required = true)
    @NotNull(message = "查询类型不能为空")
    private Integer queryType;

    /** 用户id*/
    @ApiModelProperty(value = "用户id", required = true)
    @NotNull(message = "用户id不能为空")
    private Integer userId;
}
