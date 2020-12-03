package com.yunya.feign.employee_attend.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.Date;

/**
 * 简介：考勤汇总查询参数模型
 *
 * @author: chenlin
 * @Description: 考勤汇总查询参数模型
 * @Date: 2020/11/9 15:42
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("考勤汇总查询参数模型")
public class AttendanceStatisticsQueryForm implements Serializable {

    @ApiModelProperty("是否分页,默认true")
    private Boolean whetherPage = true;

    @ApiModelProperty("页码，默认第一页")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示条数，默认10条")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    /** 员工姓名 */
    @ApiModelProperty(value = "员工姓名")
    private String employeeName;

    /** 员工id */
    @ApiModelProperty(value = "员工id")
    private Integer userId;

    /** 组织id */
    @ApiModelProperty(value = "组织id")
    private Integer orgId;

    /** 查询日期 (年查询：2020；月查询：2020-01)*/
    @ApiModelProperty(value = "查询日期 (年查询：2020；月查询：2020-01)", required = true)
    private String date;

    /** 日期查询方式：0-按月查询，1-按年查询 */
    @ApiModelProperty(value = "日期查询方式：0-按月查询，1-按年查询", required = true)
    private Byte type;

    /** 查询开始日期 */
    @ApiModelProperty(value = "查询开始日期")
    private Date betweenDate;

    /** 查询结束日期 */
    @ApiModelProperty(value = "查询结束日期")
    private Date andDate;
}
