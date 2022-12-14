package com.yunya.feign.sms.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介：短信模板设置添加模型
 *
 * @author: chenlin
 * @Description: 短信模板设置添加模型
 * @Date: 2020/12/11 20:02
 * @since: 1.0.0
 */
@ApiModel("短信模板设置添加模型")
@ToString
@Data
public class SmsTemplateSetModel implements Serializable {
    /**
     * 组织id（门诊、公司）
     */
    @ApiModelProperty("组织id（门诊、公司）")
    private Integer orgId;

    /**
     * 签名id
     */
    @ApiModelProperty(value = "签名id",required = true)
    @NotNull(message = "签名id不能为空")
    private Integer signatureId;

    /**
     * 模板名称
     */
    @ApiModelProperty(value = "模板名称",required = true)
    @NotBlank(message = "模板名称不能为空")
    private String templateName;

    /**
     * 模板内容
     */
    @ApiModelProperty(value = "模板内容",required = true)
    @NotBlank(message = "模板内容不能为空")
    private String templateContent;

    /**
     * 模板参数的占位符（0-验证码，1-患者姓名，2-诊所名称，3-诊所电话，4-诊所地址，5-预约医生姓名，6-预约时间，7-先生/女士/小朋友，8-今天/明天，9-上午/下午，10-会员充值金额，11-会员消费金额，12-会员剩余金额，13-会员卡号，14-预付款充值金额，15-预付款消费金额，16-预付款剩余金额，17-预付款账号，18-产品型号，19-产品名称，20-卡券卡号，21-卡券卡密，22-西湖益联保服务套餐，23-地址+路线）
     */
    @ApiModelProperty("模板参数的占位符（0-验证码，1-患者姓名，2-诊所名称，3-诊所电话，4-诊所地址，5-预约医生姓名，6-预约时间，7-先生/女士/小朋友，8-今天/明天，9-上午/下午，10-会员充值金额，11-会员消费金额，12-会员剩余金额，13-会员卡号，14-预付款充值金额，15-预付款消费金额，16-预付款剩余金额，17-预付款账号，18-产品型号，19-产品名称，20-卡券卡号，21-卡券卡密，22-西湖益联保服务套餐，23-地址+路线）")
    private String templateItem;

    /**
     * 申请说明
     */
    @ApiModelProperty("申请说明")
    @NotBlank(message = "申请说明不能为空")
    private String remark;

    /**
     * 适用场景：0-预约提醒，1-会员充值提醒，2-会员消费提醒，3-预付款充值提醒，4-预付款消费提醒，5-卡券售出提醒，6-考勤设备绑定验证码，7-找回密码验证码，8-卡券激活
     */
    @ApiModelProperty(value = "适用场景：0-预约提醒，1-会员充值提醒，2-会员消费提醒，3-预付款充值提醒，4-预付款消费提醒，5-卡券售出提醒，6-考勤设备绑定验证码，7-找回密码验证码，8-卡券激活",required = true)
    @NotNull(message = "适用场景不能为空")
    private Byte sense;

    /**
     * 模板位置json
     */
    @ApiModelProperty("模板位置json")
    private String tagInputValue;
}
