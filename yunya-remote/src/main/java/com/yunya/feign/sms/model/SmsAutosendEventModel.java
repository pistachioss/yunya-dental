package com.yunya.feign.sms.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介：短信自动发送提交模型
 *
 * @author: chenlin
 * @Description: 短信自动发送提交模型
 * @Date: 2020/12/16 13:23
 * @since: 1.0.0
 */
@ApiModel("短信自动发送提交模型")
@ToString
@Data
public class SmsAutosendEventModel implements Serializable {
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
     * 短信模板id
     */
    @ApiModelProperty("短信模板id")
    private Integer templateId;
}
