package com.yunya.feign.sms.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.Collection;

/**
 * 简介：短信发送记录查询模型
 *
 * @author: chenlin
 * @Description: 短信发送记录查询模型
 * @Date: 2020/12/14 14:22
 * @since: 1.0.0
 */
@ApiModel("短信发送记录查询模型")
@ToString
@Data
public class SmsSendRecordQueryForm implements Serializable {

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
     * 批次id
     */
    @ApiModelProperty("批次id")
    private Integer batchId;

    /**
     * 短信类型 0：验证码。1：短信通知。2：推广短信。3：国际/港澳台消息。
     */
    @ApiModelProperty(value = "短信类型 0：验证码。1：短信通知。2：推广短信。3：国际/港澳台消息。", required = true)
    private Byte type;

    /**
     * 组织id（门诊、公司）
     */
    @ApiModelProperty("组织id（门诊、公司）")
    private Integer orgId;

    /**
     * 接收者id
     */
    @ApiModelProperty("接收者id")
    private Integer receiverId;

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
     * 发送状态：0-发送中，1-发送成功，2-发送失败
     */
    @ApiModelProperty("发送状态：0-发送中，1-发送成功，2-发送失败")
    private Byte status;

    /**
     * 批次id列表
     */
    @ApiModelProperty("批次id列表")
    private Collection<Integer> batchIds;
}
