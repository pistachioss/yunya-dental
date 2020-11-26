package com.yunya.feign.employee_attend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class findNoWorkEmByDateVO {

    @ApiModelProperty(value = "班次Id")
    private Integer scheduleId;
    @ApiModelProperty(value = "门诊名称")
    private String companyName;
    @ApiModelProperty(value = "门诊Id")
    private Integer companyId;
    @ApiModelProperty(value = "班次开始时间")
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
    @ApiModelProperty(value = "班次结束时间")
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
    @ApiModelProperty(value = "班次名称")
    private String name;




}
