package com.yunya.feign.sms.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：短信发送情况响应模型
 *
 * @author: chenlin
 * @Description: 短信发送情况响应模型
 * @Date: 2020/12/14 14:35
 * @since: 1.0.0
 */
@ApiModel("短信发送情况响应模型")
@Data
@ToString
public class SmsSendSituationVO implements Serializable {

    /**
     * 发送对象总数
     */
    @ApiModelProperty("接收者id")
    private Integer total;

    /**
     * 发送成功的总人数
     */
    @ApiModelProperty("短信条数")
    private Integer success;

    /**
     * 发送失败的总人数
     */
    @ApiModelProperty("发送人id")
    private Integer failure;
}
