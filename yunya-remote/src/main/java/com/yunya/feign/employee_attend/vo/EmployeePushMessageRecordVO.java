package com.yunya.feign.employee_attend.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：消息推送记录VO
 *
 * @author: chenlin
 * @Description: 消息推送记录VO
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

    /** 推送消息类型*/
    @ApiModelProperty("推送消息类型：推送类型：1-考勤打卡，10-请假审批申请，20-加班审批申请，30-外勤审批申请，11-请假审批抄送，21-加班审批抄送，31-外勤审批抄送，12-请假审批通过，22-加班审批通过，32-外勤审批通过，13-请假审批未通过，23-加班审批未通过，33-外勤审批未通过，14-请假审批撤销，24-加班审批撤销，34-外勤审批撤销")
    private Integer pushType;
}
