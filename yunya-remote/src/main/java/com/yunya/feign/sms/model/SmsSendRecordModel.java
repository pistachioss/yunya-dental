package com.yunya.feign.sms.model;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 简介：短信发送模型
 *
 * @author: chenlin
 * @Description: 短信发送模型
 * @Date: 2020/12/14 16:42
 * @since: 1.0.0
 */
@ApiModel("短信发送模型")
@ToString
@Data
public class SmsSendRecordModel implements Serializable {

    /**
     * 组织id（门诊、公司）
     */
    @ApiModelProperty(value = "门诊、公司",required = true)
    @NotNull
    private Integer orgId;

    /**
     * 接受者id列表，可空，不空时必须与发送对象、手机号一一对应
     */
    @ApiModelProperty("接受者id列表，可空，不空时必须与发送对象、手机号一一对应")
    private List<Integer> receiverIds;

    /**
     * 发送对象（可能是员工姓名，或其他）
     */
    @ApiModelProperty(value = "发送对象（可能是员工姓名，或其他）",required = true)
    @NotNull
    @NotEmpty
    private List<String> sendObjects;

    /**
     * 接受短信的手机号码列表，多个手机号以“,”分隔，例如"1333333333,13555555555"
     */
    @ApiModelProperty(value = "接受短信的手机号码列表，多个手机号以“,”分隔",required = true)
    @NotBlank
    private String mobiles;

    /**
     * 短信模板id
     */
    @ApiModelProperty(value = "短信模板id",required = true)
    @NotNull
    private Integer templateId;

    /**
     * 发送人id
     */
    @ApiModelProperty(value = "发送人id",required = true)
    @NotNull
    private Integer sendUserId;

    /**
     * 发送时间
     */
    @ApiModelProperty(value = "发送时间",required = true)
    @NotNull
    private Date sendTime;


    /**
     * 短信类型 0：验证码。1：短信通知。2：推广短信。3：国际/港澳台消息
     */
    @ApiModelProperty(value = "短信类型 0：验证码。1：短信通知。2：推广短信。3：国际/港澳台消息",required = true)
    @NotNull
    private Byte type;

    /**
     * 模板变量值json: [{"code1":"25","code4":"张三"},{"code1":"30","code4":"李四"}]
     */
    @ApiModelProperty("模板变量值json，需要保证变量值的对象与手机号码一一对应，例如：[{\"code1\":\"25\",\"code4\":\"张三\"},{\"code1\":\"30\",\"code4\":\"李四\"}]")
    private JSONArray templateParamJson;
}
