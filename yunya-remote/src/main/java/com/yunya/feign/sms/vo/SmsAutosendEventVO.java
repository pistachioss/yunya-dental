package com.yunya.feign.sms.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：短信自动发送响应模型
 *
 * @author: chenlin
 * @Description: 短信自动发送响应模型
 * @Date: 2020/12/16 12:45
 * @since: 1.0.0
 */
@ApiModel("短信自动发送响应模型")
@Data
@ToString
public class SmsAutosendEventVO implements Serializable {

    /**
     * 主键id
     */
    @ApiModelProperty("主键id")
    private Integer id;

    /**
     * 组织id（门诊、公司）
     */
    @ApiModelProperty("组织id（门诊、公司）")
    private Integer orgId;

    /**
     * 事件编码
     */
    @ApiModelProperty("事件编码")
    private String eventCode;

    /**
     * 事件名称
     */
    @ApiModelProperty("事件名称")
    private String eventName;

    /**
     * 短信模板名称
     */
    @ApiModelProperty("短信模板名称")
    private String templateName;

    /**
     * 事件所对应的业务表的主键id
     */
    @ApiModelProperty("事件所对应的业务表的主键id")
    private Integer bizPid;

    /**
     * 短信模板id
     */
    @ApiModelProperty("短信模板id")
    private Integer templateId;

    /**
     * 状态：0-关闭，1-开启
     */
    @ApiModelProperty("状态：0-关闭，1-开启")
    private Byte status;
}
