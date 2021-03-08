package com.yunya.feign.sms.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：短信模板id数据模型
 *
 * @author: chenlin
 * @Description: 短信模板id数据模型
 * @Date: 2021/1/17 16:30
 * @since: 1.0.0
 */
@ApiModel("短信模板id数据模型")
@Data
@ToString
public class SmsTemplateIdRecordModel extends SmsModel implements Serializable {
    /**
     * 模板id
     */
    @ApiModelProperty("模板id")
    private Integer templateId;
}
