package com.yunya.feign.sms.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介：短信模板设置修改模型
 *
 * @author: chenlin
 * @Description: 短信模板设置修改模型
 * @Date: 2020/12/11 20:17
 * @since: 1.0.0
 */
@ApiModel("短信模板设置修改模型")
@ToString
@Data
public class SmsTemplateSetForm implements Serializable {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id",required = true)
    @NotNull
    private Integer id;

    /**
     * 组织id（门诊、公司）
     */
    @ApiModelProperty("组织id（门诊、公司）")
    private Integer orgId;

    /**
     * 签名id
     */
    @ApiModelProperty(value = "签名id",required = true)
    @NotNull
    private Integer signatureId;

    /**
     * 模板名称
     */
    @ApiModelProperty(value = "模板名称",required = true)
    @NotBlank
    private String templateName;

    /**
     * 模板内容
     */
    @ApiModelProperty(value = "模板内容",required = true)
    @NotBlank
    private String templateContent;

    /**
     * 模板参数的占位符（1-患者姓名，2-诊所名称，3-诊所电话，4-诊所地址，5-预约医生姓名，6-预约时间，7-先生/女士/小朋友，8-今天/明天，9-上午/下午，10-会员充值金额，11-会员消费金额，12-会员剩余金额，13-会员卡号，14、产品型号、15-产品名称，16-卡券卡号，17-卡券卡密）
     */
    @ApiModelProperty("模板参数的占位符（1-患者姓名，2-诊所名称，3-诊所电话，4-诊所地址，5-预约医生姓名，6-预约时间，7-先生/女士/小朋友，8-今天/明天，9-上午/下午，10-会员充值金额，11-会员消费金额，12-会员剩余金额，13-会员卡号，14、产品型号、15-产品名称，16-卡券卡号，17-卡券卡密）")
    private String templateItem;

    /**
     * 申请说明
     */
    @ApiModelProperty(value = "申请说明", required = true)
    @NotBlank
    private String remark;

    /**
     * 适用场景：0-预约提醒，1-会员充值提醒，2-会员消费提醒，3-预付款充值提醒，4-预付款消费提醒，5-卡券售出提醒，6-考勤设备绑定验证码，7-找回密码验证码
     */
    @ApiModelProperty(value = "适用场景：0-预约提醒，1-会员充值提醒，2-会员消费提醒，3-预付款充值提醒，4-预付款消费提醒，5-卡券售出提醒，6-考勤设备绑定验证码，7-找回密码验证码",required = true)
    @NotNull
    private Byte sense;
}
