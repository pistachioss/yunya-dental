package com.yunya.feign.employee_attend.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/4/6 9:06
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("消息推送记录VO")
public class EmployeePushMessageRecordVO implements Serializable {

    /** 业务数据id*/
    @ApiModelProperty("业务数据id")
    private Integer dataId;

    /** 消息标题*/
    @ApiModelProperty("消息标题")
    private String title;

    /** 消息内容*/
    @ApiModelProperty("消息内容")
    private String content;

    /** 是否已读*/
    @ApiModelProperty("是否已读")
    private Boolean hadRead;

    /** 消息推送时间*/
    @ApiModelProperty("消息推送时间")
    private String pushTime;
}
