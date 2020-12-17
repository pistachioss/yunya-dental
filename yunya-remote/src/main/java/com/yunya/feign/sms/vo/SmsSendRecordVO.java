package com.yunya.feign.sms.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介：短信发送记录响应模型
 *
 * @author: chenlin
 * @Description: 短信发送记录响应模型
 * @Date: 2020/12/14 14:35
 * @since: 1.0.0
 */
@ApiModel("短信发送记录响应模型")
@Data
@ToString
public class SmsSendRecordVO implements Serializable {

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
     * 短信发送批次id
     */
    @ApiModelProperty("短信发送批次id")
    private Integer batchId;

    /**
     * 接收者id
     */
    @ApiModelProperty("接收者id")
    private Integer receiverId;

    /**
     * 发送对象
     */
    @ApiModelProperty("发送对象")
    private String sendObject;

    /**
     * 手机号（接收者）
     */
    @ApiModelProperty("手机号")
    private String mobile;

    /**
     * 短信内容
     */
    @ApiModelProperty("短信内容")
    private String content;

    /**
     * 短信条数
     */
    @ApiModelProperty("短信条数")
    private Integer contentNum;

    /**
     * 发送状态：0-发送中，1-发送成功，2-发送失败
     */
    @ApiModelProperty("发送状态：0-发送中，1-发送成功，2-发送失败")
    private Byte status;

    /**
     * 发送回执ID
     */
    @ApiModelProperty("发送回执ID")
    private String bizId;

    /**
     * 回执错误描述
     */
    @ApiModelProperty("回执错误描述")
    private String bizMsg;

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
