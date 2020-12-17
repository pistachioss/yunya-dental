package com.yunya.feign.sms.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 简介：短信发送批次查询模型
 *
 * @author: chenlin
 * @Description: 短信发送批次查询模型
 * @Date: 2020/12/14 14:22
 * @since: 1.0.0
 */
@ApiModel("短信发送批次查询模型")
@ToString
@Data
public class SmsSendBatchQueryForm implements Serializable {

    @ApiModelProperty("是否分页,默认true")
    private Boolean whetherPage = true;

    @ApiModelProperty("页码，默认第一页")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示条数，默认10条")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    @ApiModelProperty("主键id")
    private Integer id;

    /**
     * 组织id（门诊、公司）
     */
    @ApiModelProperty("组织id（门诊、公司）")
    private Integer orgId;

    /**
     * 发送条数
     */
    @ApiModelProperty("发送条数")
    private Integer sendNum;

    /**
     * 回执id
     */
    @ApiModelProperty("回执id")
    private String bizId;

    /**
     * 短信类型 0：验证码。1：短信通知。2：推广短信。3：国际/港澳台消息。
     */
    @ApiModelProperty(value = "短信类型 0：验证码。1：短信通知。2：推广短信。3：国际/港澳台消息。", required = true)
    private Byte type;
}
