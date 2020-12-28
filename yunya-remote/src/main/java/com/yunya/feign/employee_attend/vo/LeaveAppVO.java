package com.yunya.feign.employee_attend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 简介: 请假审批VO类
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
@ApiModel
public class LeaveAppVO {

    @ApiModelProperty("请假ID")
    private Integer id;
    @ApiModelProperty("用户名称")
    private String userName;
    @ApiModelProperty("用户Id")
    private Integer userId;
    @ApiModelProperty("创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date crtTime;
    @ApiModelProperty("请假类型名称")
    private String vacationName;
    @ApiModelProperty("请假类型")
    private Integer vacationStatus;
    @ApiModelProperty("开始时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
    @ApiModelProperty("结束时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
    @ApiModelProperty("请假时长(分钟)")
    private Integer leaveTime;
    @ApiModelProperty("审批状态")
    private Integer approvalStatus;
}
