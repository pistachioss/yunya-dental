package com.yunya.feign.sms.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介：短信发送批次响应模型
 *
 * @author: chenlin
 * @Description: 短信发送批次响应模型
 * @Date: 2020/12/14 14:18
 * @since: 1.0.0
 */
@ApiModel("短信发送批次响应模型")
@Data
@ToString
public class SmsSendBatchVO implements Serializable {

    @ApiModelProperty("主键id")
    private Integer id;

    /**
     * 组织id（门诊、公司）
     */
    @ApiModelProperty("组织id（门诊、公司）")
    private Integer orgId;

    /**
     * 发送条数
     */
    @ApiModelProperty("发送条数")
    private Integer sendNum;

    /**
     * 回执id
     */
    @ApiModelProperty("回执id")
    private String bizId;

    /**
     * 回执错误信息
     */
    @ApiModelProperty("回执错误信息")
    private String bizMsg;

    /**
     * 短信类型 0：验证码。1：短信通知。2：推广短信。3：国际/港澳台消息。
     */
    @ApiModelProperty(value = "短信类型 0：验证码。1：短信通知。2：推广短信。3：国际/港澳台消息。", required = true)
    private Byte type;

    /**
     * 发送人id
     */
    @ApiModelProperty("发送人id")
    private Integer crtId;

    /**
     * 发送人
     */
    @ApiModelProperty("发送人")
    private String crtUser;

    /**
     * 发送时间
     */
    @ApiModelProperty("发送时间")
    private Date crtTime;
}
