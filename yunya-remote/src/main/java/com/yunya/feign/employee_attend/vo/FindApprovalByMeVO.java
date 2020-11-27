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
public class FindApprovalByMeVO {
    @ApiModelProperty("id")
    private Integer id;
    @ApiModelProperty("申请类型 0：加班 1:外勤 2：请假")
    private Integer appType;
    @ApiModelProperty("申请人Id")
    private Integer userId;
    @ApiModelProperty("申请人名称")
    private String userName;
    @ApiModelProperty("假期类型Id")
    private Integer vacationId;
    @ApiModelProperty("假期类型名称")
    private String vacationName;
    @ApiModelProperty("开始时间")
    private Date startTime;
    @ApiModelProperty("结束时间")
    private Date endTime;
    @ApiModelProperty("时长")
    private Integer time;
    @ApiModelProperty("创建时间")
    private Date crtTime;

}
