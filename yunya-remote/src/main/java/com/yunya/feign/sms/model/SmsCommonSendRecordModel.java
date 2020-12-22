package com.yunya.feign.sms.model;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 简介：短信单条发送记录公共模型
 *
 * @author: chenlin
 * @Description: 短信单条发送记录公共模型
 * @Date: 2020/12/19 16:42
 * @since: 1.0.0
 */
@ApiModel("短信单条发送记录公共模型")
@ToString
@Data
public class SmsCommonSendRecordModel implements Serializable {

    /**
     * 接收者id
     */
    @ApiModelProperty(value = "接收者id",required = true)
    private Integer receiverId;

    /**
     * 发送对象（可能是员工姓名，或其他）
     */
    @ApiModelProperty(value = "发送对象（可能是员工姓名，或其他）",required = true)
    @NotBlank
    private String sendObject;

    /**
     * 接收短信的手机号码
     */
    @ApiModelProperty(value = "接收短信的手机号码",required = true)
    @NotBlank
    private String mobile;

    /**
     * 模板变量值json: 例如：{"code1":"25","code4":"张三"}；注意，如果模板中重复了同一个变量，则该json对象的key由三部分组成："re"前缀 + 重复次数 + "code" + 变量项id，例如 {"code1":"25","code4":"张三","re1code1":"25"}
     */
    @ApiModelProperty(value = "模板变量值json: 例如：{\"code1\":\"25\",\"code4\":\"张三\"}；注意，如果模板中重复了同一个变量，则该json对象的key由三部分组成：\"re\"前缀 + 重复次数 + \"code\" + 变量项id，例如 {\"code1\":\"25\",\"code4\":\"张三\",\"re1code1\":\"25\"}")
    private JSONObject templateParam;
}
