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
import java.util.List;

/**
 * 简介：短信批量发送模型
 *
 * @author: chenlin
 * @Description: 短信批量发送模型
 * @Date: 2020/12/14 16:42
 * @since: 1.0.0
 */
@ApiModel("短信批量发送模型")
@ToString
@Data
public class SmsBatchSendRecordModel implements Serializable {

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
     * 模板变量值json: 需要保证变量值的对象与手机号码一一对应，例如：[{"code1":"25","code4":"张三"},{"code1":"30","code4":"李四"}]；
     * 注意，如果模板中重复了同一个变量，则该json对象的key由三部分组成："re"前缀 + 重复次数 + "code" + 变量项id，
     * 例如 [{"code1":"25","code4":"张三","re1code1":"25"},{"code1":"30","code4":"李四","re1code1":"30"}]
     */
    @ApiModelProperty("模板变量值json: 需要保证变量值的对象与手机号码一一对应，例如：[{\"code1\":\"25\",\"code4\":\"张三\"},{\"code1\":\"30\",\"code4\":\"李四\"}]；注意，如果模板中重复了同一个变量，则该json对象的key由三部分组成：\"re\"前缀 + 重复次数 + \"code\" + 变量项id，例如 [{\"code1\":\"25\",\"code4\":\"张三\",\"re1code1\":\"25\"},{\"code1\":\"30\",\"code4\":\"李四\",\"re1code1\":\"30\"}]")
    private JSONArray templateParamJson;
}
