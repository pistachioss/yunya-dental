package com.yunya.feign.sms.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 简介：短信自动发送添加模型
 *
 * @author: chenlin
 * @Description: 短信自动发送添加模型
 * @Date: 2020/12/16 13:23
 * @since: 1.0.0
 */
@ApiModel("短信自动发送添加模型")
@ToString
@Data
public class SmsAutosendEventForm implements Serializable {
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
     * 事件编码
     */
    @ApiModelProperty("事件编码")
    private String eventCode;

    /**
     * 事件名称
     */
    @ApiModelProperty("事件名称")
    private String eventName;

    /**
     * 短信模板id
     */
    @ApiModelProperty("短信模板id")
    private Integer templateId;

    /**
     * 状态：0-关闭，1-开启
     */
    @ApiModelProperty("状态：0-关闭，1-开启")
    private Byte status;

    private Integer crtId;
    private Date crtTime;
    private Integer uptId;
    private Date uptTime;
}
