package com.yunya.feign.employee_attend.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 简介:
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Data
public class BaseEmployeeScheduleVO {
    @ApiModelProperty("排班Id")
    private Integer employeeScheduleId;
    @ApiModelProperty("组织Id")
    private Integer orgId;
    @ApiModelProperty("班次ID")
    private Integer scheduleId;
    @ApiModelProperty("用户ID")
    private Integer userId;
    @ApiModelProperty("排班日期")
    private Date scheduleDate;
    @ApiModelProperty("开始时间")
    private Date startWorkTime;
    @ApiModelProperty("结束时间")
    private Date offWorkTime;
    @ApiModelProperty("排班时长(分钟)")
    private Integer scheduleDuration;

}
