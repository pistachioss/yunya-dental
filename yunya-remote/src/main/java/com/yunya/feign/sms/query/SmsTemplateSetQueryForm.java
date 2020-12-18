package com.yunya.feign.sms.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 简介：短信模板设置查询参数模型
 *
 * @author: chenlin
 * @Description: 短信模板设置查询参数模型
 * @Date: 2020/12/11 9:35
 * @since: 1.0.0
 */
@ApiModel("短信模板设置查询参数模型")
@ToString
@Data
public class SmsTemplateSetQueryForm implements Serializable {

    @ApiModelProperty("是否分页,默认true")
    private Boolean whetherPage = true;

    @ApiModelProperty("页码，默认第一页")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示条数，默认10条")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    /**
     * 组织id
     */
    @ApiModelProperty("组织id")
    private Integer orgId;


    /**
     * 适用场景：0-预约提醒，1-会员充值提醒，2-会员消费提醒，3-预付款充值提醒，4-预付款消费提醒，5-卡券售出提醒，6-考勤设备绑定验证码，7-找回密码验证码
     */
    @ApiModelProperty("适用场景：0-预约提醒，1-会员充值提醒，2-会员消费提醒，3-预付款充值提醒，4-预付款消费提醒，5-卡券售出提醒，6-考勤设备绑定验证码，7-找回密码验证码")
    private Byte sense;

    /**
     * 模板名称
     */
    @ApiModelProperty("模板名称")
    private String templateName;

    /**
     * 审批状态：0-审核中，1-审核通过，2-审核失败
     */
    @ApiModelProperty("审批状态：0-审核中，1-审核通过，2-审核失败")
    private Byte templateStatus;

    /**
     * 短信模板类型：0：验证码。1：短信通知。2：推广短信。3：国际/港澳台消息
     */
    @ApiModelProperty("短信模板类型：0：验证码。1：短信通知。2：推广短信。3：国际/港澳台消息")
    private Byte templateType;

    /**
     * 模板code
     */
    @ApiModelProperty("模板code")
    private Integer templateCode;

    /**
     * 是否需要模板预览
     */
    @ApiModelProperty("是否需要模板预览")
    private Boolean needPreview = false;
}