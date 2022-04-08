package com.yunya.modules.employeeattend.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
public class EmployeePushForm implements Serializable {
    /**
     * 要推送的员工帐号
     */
    @ApiModelProperty("要推送的员工帐号")
    private Set<Integer> empId;
    /**
     * 推送设备列表
     */
    @ApiModelProperty("推送设备列表")
    private List<String> userList;
    /** 平台*/
    @ApiModelProperty("平台")
    private int platform;
    /** 消息标题*/
    @ApiModelProperty("消息标题")
    private String title;
    /** 消息内容*/
    @ApiModelProperty("消息内容")
    private String content;
    private String showName;
    /** 是否定时推送*/
    @ApiModelProperty("是否定时推送")
    private Boolean isSchedule;
    /** 定时推送时间*/
    @ApiModelProperty("定时推送时间")
    private String scheTime;
    /** 业务数据id*/
    @ApiModelProperty("业务数据id")
    private List<Integer> ids;

    /** 操作人id */
    @ApiModelProperty("操作人id")
    public Integer optId = -999;
}
