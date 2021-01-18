package com.yunya.feign.sms.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简介：短信自动发送事件数据模型
 *
 * @author: chenlin
 * @Description: 短信自动发送事件数据模型
 * @Date: 2021/1/17 16:30
 * @since: 1.0.0
 */
@ApiModel("短信自动发送事件数据模型")
@Data
@ToString
public class SmsAutoEventSendRecordModel implements Serializable {
    /**
     * 组织ID
     */
    @ApiModelProperty("组织ID")
    private Integer orgId;
    /**
     * 员工id
     */
    @ApiModelProperty("员工id")
    private Integer userId;
    /**
     * 员工姓名
     */
    @ApiModelProperty("员工姓名")
    private String name;
    /**
     * 自动发送事件
     */
    @ApiModelProperty("自动发送事件")
    private String eventCode;
    /**
     * 短信参数
     */
    @ApiModelProperty("短信参数")
    private List<? extends SmsCommonSendRecordModel> models;
}
