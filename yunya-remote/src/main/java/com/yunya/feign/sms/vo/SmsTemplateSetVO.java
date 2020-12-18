package com.yunya.feign.sms.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：短信模板设置响应模型
 *
 * @author: chenlin
 * @Description: 短信模板设置响应模型
 * @Date: 2020/12/11 17:43
 * @since: 1.0.0
 */
@ApiModel("短信模板设置响应模型")
@ToString
@Data
public class SmsTemplateSetVO implements Serializable {

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
     * 签名id
     */
    @ApiModelProperty("签名id")
    private Integer signatureId;

    /**
     * 签名名称
     */
    @ApiModelProperty("签名id")
    private String signName;

    /**
     * 模板名称
     */
    @ApiModelProperty("模板名称")
    private String templateName;

    /**
     * 模板内容
     */
    @ApiModelProperty("模板内容")
    private String templateContent;

    /**
     * 模板参数的占位符（1-患者姓名，2-诊所名称，3-诊所电话，4-诊所地址，5-预约医生姓名，6-预约时间，7-先生/女士/小朋友，8-今天/明天，9-上午/下午，10-会员充值金额，11-会员消费金额，12-会员剩余金额，13-会员卡号，14、产品型号、15-产品名称，16-卡券卡号，17-卡券卡密）
     */
    @ApiModelProperty("模板参数的占位符（1-患者姓名，2-诊所名称，3-诊所电话，4-诊所地址，5-预约医生姓名，6-预约时间，7-先生/女士/小朋友，8-今天/明天，9-上午/下午，10-会员充值金额，11-会员消费金额，12-会员剩余金额，13-会员卡号，14、产品型号、15-产品名称，16-卡券卡号，17-卡券卡密）")
    private String templateItem;

    /**
     * 模板内容预览
     */
    @ApiModelProperty("模板内容预览")
    private String templateContentPreview;

    /**
     * 模板有效字数（包含头部的签名，不包含模板变量及其占位符）
     */
    @ApiModelProperty("模板有效字数（包含头部的签名，不包含模板变量及其占位符）")
    private Integer templateLength;

    /**
     * 申请说明
     */
    @ApiModelProperty("申请说明")
    private String remark;

    /**
     * 审核状态：0：审核中。1：审核通过。2：审核失败。
     */
    @ApiModelProperty("审核状态：0：审核中。1：审核通过。2：审核失败。")
    private Byte templateStatus;

    /**
     * 阿里云短信模板code
     */
    @ApiModelProperty("阿里云短信模板code")
    private String templateCode;

    /**
     * 短信模板类型：0：验证码。1：短信通知。2：推广短信。3：国际/港澳台消息。
     */
    @ApiModelProperty("短信模板类型：0：验证码。1：短信通知。2：推广短信。3：国际/港澳台消息。")
    private Byte templateType;

    /**
     * 提交人
     */
    @ApiModelProperty("提交人")
    private String crtUser;

    /**
     * 适用场景：0-预约提醒，1-会员充值提醒，2-会员消费提醒，3-预付款充值提醒，4-预付款消费提醒，5-卡券售出提醒，6-考勤设备绑定验证码，7-找回密码验证码
     */
    @ApiModelProperty("适用场景：0-预约提醒，1-会员充值提醒，2-会员消费提醒，3-预付款充值提醒，4-预付款消费提醒，5-卡券售出提醒，6-考勤设备绑定验证码，7-找回密码验证码")
    private Byte sense;
}
