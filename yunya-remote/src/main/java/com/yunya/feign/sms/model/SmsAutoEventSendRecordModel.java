package com.yunya.feign.sms.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

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
@ToString(callSuper = true)
public class SmsAutoEventSendRecordModel extends SmsModel implements Serializable {
    /**
     * 自动发送事件
     */
    @ApiModelProperty("自动发送事件")
    private String eventCode;
}
